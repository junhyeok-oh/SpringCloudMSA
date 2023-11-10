package com.example.userservice.client;

import com.example.userservice.vo.ResponseOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


// 유레카에 등록된 마이크로서비스들 중,"order-service" 이름으로 검색된 서비스를 검색
@FeignClient(name = "order-service") // application name
public interface OrderServiceClient {

    @GetMapping("/order-service/{userId}/orders") // 호출하고 싶은 마이크로서비스의 url 그대로 붙여넣기
    List<ResponseOrder> getOrders(@PathVariable("userId") String userId);
}
