package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;


public class EmployeeAction extends ActionBase {

    private EmployeeService service;

    //メソッド実行
    @Override
    public void process() throws ServletException, IOException{
    service = new EmployeeService();

    invoke();

    service.close();

}

//一覧画面に表示
public void index() throws ServletException, IOException{
  //管理者かどうかのチェック //追記
    if (checkAdmin()) { //追記
int page = getPage();
List<EmployeeView> employees = service.getPerPage(page);

//全従業員データの件数
long employeeCount = service.countAll();

putRequestScope(AttributeConst.EMPLOYEES,employees);
putRequestScope(AttributeConst.EMP_COUNT,employeeCount);
putRequestScope(AttributeConst.PAGE,page);
putRequestScope(AttributeConst.MAX_ROW,JpaConst.ROW_PER_PAGE);

//セッションにフラッシュメッセージを設定
String flush =getSessionScope(AttributeConst.FLUSH);
if (flush != null) {
    putRequestScope(AttributeConst.FLUSH,flush);
    removeSessionScope(AttributeConst.FLUSH);
}

forward(ForwardConst.FW_EMP_INDEX);
}
}

//新規登録画面を表示する
public void entryNew() throws ServletException,IOException{
  //管理者かどうかのチェック //追記
    if (checkAdmin()) { //追記
    putRequestScope(AttributeConst.TOKEN,getTokenId());
    putRequestScope(AttributeConst.EMPLOYEE, new EmployeeView());

    forward(ForwardConst.FW_EMP_NEW);
}
}
//新規登録を行う
public void create() throws ServletException,IOException{
  //管理者かどうかのチェック //追記
    if (checkAdmin()&& checkToken()) {
        EmployeeView ev = new EmployeeView(
                null,
                getRequestParam(AttributeConst.EMP_CODE),
                getRequestParam(AttributeConst.EMP_NAME),
                getRequestParam(AttributeConst.EMP_PASS),
                toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                null,
                null,
                AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

        //アプリケーションスコープからpepper文字列を取得
        String pepper = getContextScope(PropertyConst.PEPPER);

        //従業員情報登録
        List<String> errors = service.create(ev, pepper);

        if (errors.size() > 0) {
            //登録中にエラーがあった場合

            putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
            putRequestScope(AttributeConst.EMPLOYEE, ev); //入力された従業員情報
            putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

            //新規登録画面を再表示
            forward(ForwardConst.FW_EMP_NEW);

        } else {
            //登録中にエラーがなかった場合

            //セッションに登録完了のフラッシュメッセージを設定
            putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

            //一覧画面にリダイレクト
            redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
        }
    }
}
//show
public void show() throws ServletException, IOException {
  //管理者かどうかのチェック //追記
    if (checkAdmin()) { //追記
    //idを条件に従業員データを取得する
    EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

    if (ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {

        //データが取得できなかった、論理削除されている場合はエラー画面表示
        forward(ForwardConst.FW_ERR_UNKNOWN);
        return;
    }

    putRequestScope(AttributeConst.EMPLOYEE, ev); //取得した従業員情報

    //詳細画面を表示
    forward(ForwardConst.FW_EMP_SHOW);
    }
}
//edit

public void edit() throws ServletException,IOException {
  //管理者かどうかのチェック //追記
    if (checkAdmin()) { //追記
    EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));
            if(ev == null || ev.getDeleteFlag() == AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {


            //取得できなかった、論理的削除の場合エラー画面を表示
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
}

putRequestScope(AttributeConst.TOKEN,getTokenId());
putRequestScope(AttributeConst.EMPLOYEE,ev);

forward(ForwardConst.FW_EMP_EDIT);
}
}
//update
public void update() throws ServletException,IOException{

    if (checkAdmin()&& checkToken()) {
        EmployeeView ev = new EmployeeView(
                toNumber(getRequestParam(AttributeConst.EMP_ID)),
                getRequestParam(AttributeConst.EMP_CODE),
                getRequestParam(AttributeConst.EMP_NAME),
                getRequestParam(AttributeConst.EMP_PASS),
                toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                null,
                null,
                AttributeConst.DEL_FLAG_FALSE.getIntegerValue());
        //
        String pepper = getContextScope(PropertyConst.PEPPER);

        //従業員情報更新
        List<String> errors = service.update(ev, pepper);

        if (errors.size() > 0) {
            //更新中にエラー発生した場合
            putRequestScope(AttributeConst.TOKEN,getTokenId());
            putRequestScope(AttributeConst.EMPLOYEE,ev);
            putRequestScope(AttributeConst.ERR,errors);

            //編集画面を再表示
            forward(ForwardConst.FW_EMP_EDIT);
        } else {

            putSessionScope(AttributeConst.FLUSH,MessageConst.I_UPDATED.getMessage());

            redirect(ForwardConst.ACT_EMP,ForwardConst.CMD_INDEX);

        }

    }
}

//destroy
public void destroy() throws ServletException, IOException {
    if (checkAdmin() && checkToken()) { //追記
        service.destroy(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        putSessionScope(AttributeConst.FLUSH, MessageConst.I_DELETED.getMessage());

        redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);

    }
}

//管理者かどうかチェック
private boolean checkAdmin() throws ServletException, IOException {

    EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

    if(ev.getAdminFlag() != AttributeConst.ROLE_ADMIN.getIntegerValue()) {
        forward(ForwardConst.FW_ERR_UNKNOWN);
        return false;

    } else {
        return true;
    }
    }

}
