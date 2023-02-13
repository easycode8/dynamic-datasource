package com.easycode8.datasource.dynamic.sample.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.easycode8.datasource.dynamic.core.annotation.DynamicSource;
import com.easycode8.datasource.dynamic.core.transaction.annotation.DynamicTransactional;
import com.easycode8.datasource.dynamic.sample.mapper.AccountMapper;
import com.easycode8.datasource.dynamic.sample.model.Account;
import com.easycode8.datasource.dynamic.sample.service.AccountService;
import liquibase.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private OtherService otherService;


    @DynamicSource("#account.name")
    //TODO 【默认事务管理】开启事务后,会导致多数据源切换失效
    // 1. SqlSessionUtils.getSqlSession(SqlSessionFactory sessionFactory)
    // 会获取 SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
    // 2. 如果开启了 事务则mybatis都会从事务中获取同一个SqlSessionHolder session相同的会话
    // 因为开启事务, DataSourceTransactionManager.doBegin 连接都是从txObject.getConnectionHolder()获取并且绑定到
    // TransactionSynchronizationManager.bindResource(obtainDataSource(), txObject.getConnectionHolder())
    // 综上: 开启spring事务后,只会使用事务开启时刻数据源
//    @Transactional(rollbackFor = Exception.class)
    //TODO 【自定义事务管理器】自定义dynamicDataSourceTransactionManager不能用 mybatis定义事务对象SpringManagedTransaction持有了第一个从connectionHolder获取
    // 的数据库连接
    //@Transactional(rollbackFor = Exception.class, transactionManager = "dynamicDataSourceTransactionManager")
    //TODO 【自定义事务注解】
    @DynamicTransactional
    public void createAccount(Account account) {
        String username = account.getUsername();
        this.doCreateAccount(account);
        this.otherService.createAccountDb2(account);
        this.otherService.createAccountDb1(account);
//        this.doCreateAccount(account);
        if (StringUtils.equalsIgnoreCaseAndEmpty(account.getName(), username)) {
            throw new RuntimeException("模拟触发异常");
        }

    }


    @DynamicSource
    @Override
    public List<Account> listAll() {
        return this.accountMapper.selectList(Wrappers.emptyWrapper());
    }


    private void doCreateAccount(Account account) {
        account.setUsername(UUID.randomUUID().toString().replace("-",""));
        this.accountMapper.insert(account);
        //特定用户抛出异常,测试事务功能
        if ("error".equals(account.getName())) {
            throw new RuntimeException("模拟触发异常");
        }
    }




}
