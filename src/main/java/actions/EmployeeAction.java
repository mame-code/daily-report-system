package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
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
}