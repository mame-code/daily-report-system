package models.validators;

import java.util.ArrayList;
import java.util.List;

import actions.views.EmployeeView;
import constants.MessageConst;
import services.EmployeeService;


//
public class EmployeeValidator {

    //
public static List<String> validate(
EmployeeService service, EmployeeView ev,Boolean
codeDuplicateCheckFlag,Boolean passwordCheckFlag){
List<String> errors = new ArrayList<String>();

//社員番号のチェック
String codeError = validateCode(service, ev.getCode(),
        codeDuplicateCheckFlag);
if (!codeError.equals("")) {
    errors.add(codeError);
}
    //氏名チェック
    String nameError = validateName(ev.getName());
            if (!nameError.equals("")) {
                errors.add(nameError);
}
            //パスワードチェック
            String passError = validatePassword(ev.getPassword(),
                    passwordCheckFlag);
            if (!passError.equals("")) {
                errors.add(passError);
}
            return errors;
}

//社員番号の入力チェックを行いエラーメッセージを返却
private static String validateCode(EmployeeService service, String code,
        Boolean codeDuplicateCheckFlag) {

//入力値がなければエラーメッセージを返却
if (code == null || code.equals("")) {
    return MessageConst.E_NOEMP_CODE.getMessage();
}
if (codeDuplicateCheckFlag) {
    //社員番号の重複チェック実施

    long employeesCount = isDuplicateEmployee(service,code);

            //同一社員番号が既に登録されている場合
            if(employeesCount > 0) {
                return MessageConst.E_EMP_CODE_EXIST.getMessage();
            }
}
//エラーがない場合はから文字を返却
return "";
}

private static long isDuplicateEmployee(EmployeeService service, String code) {
    long employeesCount = service.countByCode(code);
return employeesCount;
}

//氏名に入力ちがあるかチェックし入力値がなければエラーメッセージを返却
private static String validateName(String name) {

//入力値がなければエラーメッセージを返却
if(name == null || name.equals("")) {
  return MessageConst.E_NONAME.getMessage();
}
return "";
}

//パスワードに入力ちがあるかチェックし入力値がなければエラーメッセージを返却
private static String validatePassword(String password,Boolean
        passwordCheckFlag) {

//入力値がなければエラーメッセージを返却
if (passwordCheckFlag && (password == null || password.equals(""))){
return MessageConst.E_NOPASSWORD.getMessage();
}
return "";
}
}
