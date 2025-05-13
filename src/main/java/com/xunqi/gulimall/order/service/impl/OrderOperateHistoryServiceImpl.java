package com.xunqi.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.xunqi.gulimall.order.dao.OrderOperateHistoryDao;
import com.xunqi.gulimall.order.entity.OrderOperateHistoryEntity;
import com.xunqi.gulimall.order.service.OrderOperateHistoryService;


@Service("orderOperateHistoryService")
public class OrderOperateHistoryServiceImpl extends ServiceImpl<OrderOperateHistoryDao, OrderOperateHistoryEntity> implements OrderOperateHistoryService {



}