package com.easycode8.datasource.dynamic.sample.service;




import com.easycode8.datasource.dynamic.sample.model.Account;

import java.util.List;

public interface AccountService {

    void createAccount(Account account);

    List<Account> listAll();
}
