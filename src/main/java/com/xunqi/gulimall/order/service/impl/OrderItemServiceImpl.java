package com.xunqi.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xunqi.gulimall.order.dao.OrderItemDao;
import com.xunqi.gulimall.order.entity.OrderItemEntity;
import com.xunqi.gulimall.order.entity.OrderReturnReasonEntity;
import com.xunqi.gulimall.order.service.OrderItemService;

import org.springframework.stereotype.Service;

import java.util.Map;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {



}