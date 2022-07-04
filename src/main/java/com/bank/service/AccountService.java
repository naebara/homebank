package com.bank.service;

import com.bank.config.DatabaseConnectionConfiguration;
import com.bank.model.domain.Account;
import com.bank.model.dto.AccountDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class AccountService {

    @Autowired
    private final DatabaseConnectionConfiguration databaseConnectionConfiguration;
    ModelMapper mapper = new ModelMapper();
    private final R2dbcEntityTemplate template;

    public AccountService(DatabaseConnectionConfiguration connectionConfiguration) {
        databaseConnectionConfiguration = connectionConfiguration;
        template = new R2dbcEntityTemplate(DatabaseClient.builder().connectionFactory(
                databaseConnectionConfiguration.connectionFactory()
        ).build());
    }

    public Flux<AccountDto> getAllAccounts() {
        return template.select(Account.class)
                .all()
                .map(a -> mapper.map(a, AccountDto.class));
    }

    public Mono<AccountDto> getById(Integer id) {
        return null;
    }

    public Mono<AccountDto> deleteAccountById(Integer id) {
        return null;
    }

    public Flux<AccountDto> getAccountsForCustomer(Integer id) {

        return null;
    }

    public Mono<AccountDto> createAccount(AccountDto accountDto) {
        return null;
    }
}
