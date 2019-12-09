package com.example.demo;

import com.example.demo.dao.OrderDelayTrackingMapper;
import com.example.demo.entity.OrderDelayTracking;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

	@Autowired
	private OrderDelayTrackingMapper orderDelayTrackingMapper;

	@Test
	void contextLoads() {
	}
	@Test
	public void testUserMapper(){
		List<OrderDelayTracking> l = orderDelayTrackingMapper.getByOrderId(271L);
		Assert.isTrue(l.size()==2,"!=2");
	}
}


