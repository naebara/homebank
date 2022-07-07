package com.bank.service;

import com.bank.config.DatabaseConnectionConfiguration;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.CustomerNotFoundException;
import com.bank.model.domain.Account;
import com.bank.model.domain.Customer;
import com.bank.model.dto.AccountDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.r2dbc.query.Criteria.where;


@Service
public class AccountService {

    @Autowired
    private final DatabaseConnectionConfiguration databaseConnectionConfiguration;
    private final R2dbcEntityTemplate template;
    ModelMapper mapper = new ModelMapper();

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
        return template
                .selectOne(Query.query(where("id").is(id)), Account.class)
                .switchIfEmpty(Mono.error(new AccountNotFoundException(id)))
                .map(account -> mapper.map(account, AccountDto.class));
    }

    public Mono<Integer> deleteAccountById(Integer id) {
        return template.delete(Query.query(where("id").is(id)), Account.class);
    }

    public Flux<AccountDto> getAccountsForCustomer(Integer id) {
        return template
                .selectOne(Query.query(where("id").is(id)), Customer.class)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(id)))
                .flatMapMany(customer -> template.select(Query.query(where("customer_id").is(id)), Account.class))
                .map(account -> mapper.map(account, AccountDto.class));
    }

    public Mono<AccountDto> createAccount(AccountDto accountDto) {
        Account account = mapper.map(accountDto, Account.class);
        return template.insert(Account.class)
                .using(account)
                .map(a -> mapper.map(a, AccountDto.class));
    }

    public Mono<AccountDto> updateAccount(AccountDto accountDto) {
        Account account = mapper.map(accountDto, Account.class);
        return template
                .update(account)
                .onErrorMap(err -> err instanceof DataIntegrityViolationException ?
                        new CustomerNotFoundException(accountDto.getCustomerId())
                        : new AccountNotFoundException(accountDto.getId()))
                .map(resultAccount -> mapper.map(resultAccount, AccountDto.class));

    }
}
