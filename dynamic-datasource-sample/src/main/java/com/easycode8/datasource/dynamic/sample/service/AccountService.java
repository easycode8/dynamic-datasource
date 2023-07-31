package com.easycode8.datasource.dynamic.sample.service;




import com.easycode8.datasource.dynamic.sample.model.Account;

import java.util.List;

public interface AccountService {

    void createAccount(Account account);

    List<Account> listAll();

    /**
     * 测试使用请求参数切换数据源
     * @param dbType
     * @return
     */
    List<Account> listAllByDbType(String dbType);
}
