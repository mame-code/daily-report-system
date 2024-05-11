package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;


//認証に関する処理を行う

public class AuthAction extends ActionBase {
private EmployeeService service;

@Override
public void process() throws ServletException,IOException {
    service = new EmployeeService();


//メソッド実行
invoke();

service.close();
}

//ログイン画面を表示
public void showLogin() throws ServletException, IOException {

    putRequestScope(AttributeConst.TOKEN,getTokenId());

    String flush = getSessionScope(AttributeConst.FLUSH);
    if (flush != null) {
        putRequestScope(AttributeConst.FLUSH,flush);
        removeSessionScope(AttributeConst.FLUSH);
    }
    forward(ForwardConst.FW_LOGIN);

}

//ログイン処理
public void login() throws ServletException, IOException {
    String code = getRequestParam(AttributeConst.EMP_CODE);
    String plainPass = getRequestParam(AttributeConst.EMP_PASS);
    String pepper = getContextScope(PropertyConst.PEPPER);
//有効な従業員か認証する
    Boolean isValidEmployee = service.validateLogin(code, plainPass, pepper);

if(isValidEmployee) {

    if(checkToken()) {
//ログインした従業員のDBデータを取得する
        EmployeeView ev = service.findOne(code, plainPass, pepper);
        //セッションにログインした従業員を設定
        putSessionScope(AttributeConst.LOGIN_EMP,ev);
        putSessionScope(AttributeConst.FLUSH,MessageConst.I_LOGINED.getMessage());
        redirect(ForwardConst.ACT_TOP,ForwardConst.CMD_INDEX);
    }
   } else {
       putRequestScope(AttributeConst.TOKEN,getTokenId());
       putRequestScope(AttributeConst.LOGIN_ERR,true);
       putRequestScope(AttributeConst.EMP_CODE,code);

       forward(ForwardConst.FW_LOGIN);
   }
}
}
