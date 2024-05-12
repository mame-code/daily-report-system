package actions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import services.ReportService;

public class ReportAction extends ActionBase {
    private ReportService service;

    //メソッド実行
    @Override
    public void process() throws ServletException, IOException {
        service = new  ReportService();

        invoke();
        service.close();
    }

    //一覧画面を表示
    public void index() throws ServletException, IOException {

        int page = getPage();
        List<ReportView> reports = service.getAllPerPage(page);

        long reportsCount = service.countAll();

        putRequestScope(AttributeConst.REPORTS, reports);
        putRequestScope(AttributeConst.REP_COUNT, reportsCount);
        putRequestScope(AttributeConst.PAGE, page);
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE);

        //
        String flush = getSessionScope(AttributeConst.FLUSH);
        if (flush != null) {
            putRequestScope(AttributeConst.FLUSH,flush);
            removeSessionScope(AttributeConst.FLUSH);
        }
        forward(ForwardConst.FW_REP_INDEX);
    }
    //entryNew
    public void entryNew() throws ServletException,IOException {
        putRequestScope(AttributeConst.TOKEN,getTokenId());

        //今日の日付を設定
        ReportView rv= new ReportView();
        rv.setReportDate(LocalDate.now());
        putRequestScope(AttributeConst.REPORT, rv);

        forward(ForwardConst.FW_REP_NEW);
    }

    //create
    public void create() throws ServletException,IOException {
        if(checkToken()) {

            LocalDate day = null;
            if (getRequestParam(AttributeConst.REP_DATE) == null
                || getRequestParam(AttributeConst.REP_DATE).equals("")) {
                day = LocalDate.now();
                } else {
                    day = LocalDate.parse(getRequestParam(AttributeConst.REP_DATE));
                }
            //セッションからログイン中の従業員情報を取得
           EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);
           //   日報情報をインスタンス
            ReportView rv = new ReportView(
                    null,
                    ev,//ログインしている人を作成者とする
                    day,
                    getRequestParam(AttributeConst.REP_TITLE),
                    getRequestParam(AttributeConst.REP_CONTENT),
                    null,
                    null);

            //日報情報登録
           List<String> errors = service.create(rv);

           if (errors.size() > 0) {

               putRequestScope(AttributeConst.TOKEN, getTokenId());
               putRequestScope(AttributeConst.REPORT,rv);
               putRequestScope(AttributeConst.ERR,errors);

               forward(ForwardConst.FW_REP_NEW);

           } else {

               putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

               redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);


        }
    }
    }

    //show
    public void show() throws ServletException, IOException {
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        if (rv == null) {
            forward(ForwardConst.FW_ERR_UNKNOWN);
        } else {
            putRequestScope(AttributeConst.REPORT, rv);

            forward(ForwardConst.FW_REP_SHOW);
        }
     }

    //edit
    public void edit() throws ServletException,IOException {
        ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

        EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

        if (rv == null || ev.getId() != rv.getEmployee().getId()) {

            forward(ForwardConst.FW_ERR_UNKNOWN);
        } else {

            putRequestScope(AttributeConst.TOKEN,getTokenId());
            putRequestScope(AttributeConst.REPORT,rv);

            forward(ForwardConst.FW_REP_EDIT);

        }
    }

}
