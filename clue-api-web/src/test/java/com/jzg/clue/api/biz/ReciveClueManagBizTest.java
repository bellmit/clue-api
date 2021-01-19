package com.jzg.clue.api.biz;

import com.jzg.clue.service.model.UserChannel;
import com.jzg.common.service.model.User;
import com.jzg.framework.core.vo.ResultVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by JZG on 2017/10/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/spring/spring-context.xml")
public class ReciveClueManagBizTest {

    @Resource
    private ReciveBxwClueManagBiz reciveBxwClueManagBiz ;
    @Resource
    private ReciveClueManagBiz reciveClueManagBiz ;

    @Test
    public  void Test(){
       ResultVo<Boolean> resultVo= reciveBxwClueManagBiz.existsBxwClueMakeName("宝马");
        System.out.println("****************************************");
        System.out.println("status:"+resultVo.getStatus()+",is:"+resultVo.getData());
    }

   @Test
   public  void  TestgetUserChannel()
   {
       UserChannel userChannel=reciveClueManagBiz.getUserChannelByGUID("33f886aa-24a6-4335-8e36-0304215e1e7f");
       System.out.println("****************************************");
       System.out.println("userChannelId = " + userChannel.getId()+",userChannelName:"+userChannel.getChannelname());

   }
}