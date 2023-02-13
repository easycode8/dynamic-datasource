package com.easycode8.datasource.dynamic.sample.service.impl;



import com.easycode8.datasource.dynamic.core.annotation.DynamicSource;
import com.easycode8.datasource.dynamic.core.transaction.annotation.DynamicTransactional;

import com.easycode8.datasource.dynamic.sample.model.Account;
import com.easycode8.datasource.dynamic.sample.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OtherService {
    @Autowired
    private AccountMapper accountMapper;

    @DynamicTransactional
    @DynamicSource("db1")
    public void createAccountDb1(Account account) {
        this.doCreateAccount(account);
    }

    @DynamicTransactional
    @DynamicSource("db2")
//    @Transactional(rollbackFor = Exception.class)
    public void createAccountDb2(Account account) {
        this.doCreateAccount(account);
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
