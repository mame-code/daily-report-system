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


//新規登録画面を表示する
public void entryNew() throws ServletException,IOException{
    putRequestScope(AttributeConst.TOKEN,getTokenId());
    putRequestScope(AttributeConst.EMPLOYEE, new EmployeeView());

    forward(ForwardConst.FW_EMP_NEW);
}

//新規登録を行う
public void create() throws ServletException,IOException{
    if(checkToken()) {
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
}