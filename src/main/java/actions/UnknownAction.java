package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import constants.ForwardConst;


//エラー発生時処理を行うactionクラス
public class UnknownAction extends ActionBase {
    //お探しのページは見つかりませんでしたを表示す
    @Override
   public void process() throws ServletException, IOException {

   //エラー画面表示
   forward(ForwardConst.FW_ERR_UNKNOWN);
}

}
