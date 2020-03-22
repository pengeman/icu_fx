/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.peng.icu.action;

import org.peng.icu.ICU_face;
import org.peng.icu.rabbitmq.tran.RecListener;

/**
 * send & rec
 */
public class SR {
    public void sendMSG(String msg){
        org.peng.icu.rabbitmq.tran.Send.sendMSG(msg);
    }
    public void sendDOC(String filename){
        org.peng.icu.rabbitmq.tran.Send.sendDOC(filename);
    }
    public void rec(){
        org.peng.icu.rabbitmq.tran.Rec.rec(new RecListener() {
            public void msgRec(byte[] msg) {
                System.out.println(new String(msg));
            }

            public void docRec(byte[] bytes) {

            }
        });
    }
}
