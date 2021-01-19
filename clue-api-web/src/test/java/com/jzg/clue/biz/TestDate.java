package com.jzg.clue.biz;


import com.jzg.framework.utils.encrypt.Md5Encrypt;

/**
 * Created by JZG on 2017/4/26.
 */
public class TestDate {

    public static void main(String[] args) {
      // String str="{'Sign':'fefbecd2-1417-4ba5-8cbf-4bf07a100bb6','StyleName': '宝马x运动旅行车(进口) 2015款 1.5T 自动','CityName': '上海','RegDate':'2015-06-01','Mileage':6.8,'ClueType': '买车','ContactsName':'张小姐','ContactsPhone':'13161449047'}";
//        ClueManagVo clueManagVo=JSON.toJavaObject(,ClueManagVo.class);
        //JSONObject json = (JSONObject) JSONObject.parse(str);
        //ClueManagVo clueManagVo= json.toJavaObject(ClueManagVo.class);
        //System.out.println(clueManagVo.getMileage());
       /* try {
            Long s= new Date().getTime();
            System.out.println(s);
            SimpleDateFormat format = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String d = format.format(s);
            Date date=format.parse(d);
            System.out.println(date);
        } catch (Exception e) {
        }*/
        String paramStr="channelid=90clueid=240559b20fe68-8767-4199-9864-294548da50ff";
        String newSign = Md5Encrypt.toHexString(Md5Encrypt.encrypt(paramStr, "UTF-8"));
        String paramStr1="clueid=22884807b8df353e94a9b9e0be75ce246612f";
        String newSign1 = Md5Encrypt.toHexString(Md5Encrypt.encrypt(paramStr1, "UTF-8"));
        //System.out.print(newSign);
        System.out.print(newSign);

    }


}
