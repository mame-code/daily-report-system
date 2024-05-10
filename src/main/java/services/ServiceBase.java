package services;

import javax.persistence.EntityManager;

import utils.DBUtil;

//DB接続に関わる共通クラス
public class ServiceBase {
    //Entityインスタンス
    protected EntityManager em = DBUtil.createEntityManager();

            //EntityManagerクローズ
            public void close() {
        if (em.isOpen()) {
            em.close();
        }
    }

}
