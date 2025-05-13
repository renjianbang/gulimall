package com.xunqi.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.xunqi.gulimall.order.dao.OrderReturnReasonDao;
import com.xunqi.gulimall.order.entity.OrderReturnReasonEntity;
import com.xunqi.gulimall.order.service.OrderReturnReasonService;


@Service("orderReturnReasonService")
public class OrderReturnReasonServiceImpl extends ServiceImpl<OrderReturnReasonDao, OrderReturnReasonEntity> implements OrderReturnReasonService {



}