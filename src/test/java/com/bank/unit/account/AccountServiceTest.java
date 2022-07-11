package com.bank.unit.account;

import com.bank.model.domain.Account;
import com.bank.model.domain.Customer;
import com.bank.model.dto.AccountDto;
import com.bank.service.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveInsertOperation;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;


    private R2dbcEntityTemplate template = Mockito.mock(R2dbcEntityTemplate.class);

    @BeforeEach
    public void init() {
        System.out.println("macar");
        MockitoAnnotations.initMocks(this);
    }

    List<Account> accounts = Arrays.asList(Account.builder().id(1).customerId(1).currency("EUR").iban("GB82WEST12345698765432").amount(new BigDecimal("20.0")).issuedAt(LocalDate.of(2022, Month.MAY, 7)).build(), Account.builder().id(2).customerId(2).currency("DDD").iban("WHAAT").amount(new BigDecimal(100)).issuedAt(LocalDate.now()).build(), Account.builder().id(3).customerId(1).currency("EUR").iban("GB91BARC20031863198927").amount(new BigDecimal(300)).issuedAt(LocalDate.of(2023, 1, 2)).build());

    List<AccountDto> accountsDtos = Arrays.asList(AccountDto.builder().id(1).customerId(1).currency("EUR").iban("GB82WEST12345698765432").amount(new BigDecimal("20.0")).issuedAt(LocalDate.of(2022, Month.MAY, 7)).build(), AccountDto.builder().id(2).customerId(2).currency("DDD").iban("WHAAT").amount(new BigDecimal(100)).issuedAt(LocalDate.now()).build(), AccountDto.builder().id(3).customerId(1).currency("EUR").iban("GB91BARC20031863198927").amount(new BigDecimal(300)).issuedAt(LocalDate.of(2023, 1, 2)).build());


    @Test
    public void getAllAccounts() {

        ReactiveSelectOperation.ReactiveSelect<Account> reactiveOperation = getReactiveOperation();
        when(template.select(Account.class)).thenReturn(reactiveOperation);

        Flux<AccountDto> accounts = accountService.getAllAccounts().log();

        StepVerifier.create(accounts).expectNext(accountsDtos.get(0), accountsDtos.get(1), accountsDtos.get(2)).verifyComplete();
    }

    private ReactiveSelectOperation.ReactiveSelect<Account> getReactiveOperation() {
        ReactiveSelectOperation.ReactiveSelect<Account> reactiveOperation = new ReactiveSelectOperation.ReactiveSelect<Account>() {
            @Override
            public <R> ReactiveSelectOperation.SelectWithQuery<R> as(Class<R> resultType) {
                return null;
            }

            @Override
            public ReactiveSelectOperation.SelectWithProjection<Account> from(SqlIdentifier table) {
                return null;
            }

            @Override
            public ReactiveSelectOperation.TerminatingSelect<Account> matching(Query query) {
                return null;
            }

            @Override
            public Mono<Long> count() {
                return null;
            }

            @Override
            public Mono<Boolean> exists() {
                return null;
            }

            @Override
            public Mono<Account> first() {
                return null;
            }

            @Override
            public Mono<Account> one() {
                return null;
            }

            @Override
            public Flux<Account> all() {
                return Flux.fromIterable(accounts);
            }
        };
        return reactiveOperation;
    }


    @Test
    public void getAccountById() {

        when(template.selectOne(any(), any())).thenReturn(Mono.just(accounts.get(0)));
        Mono<AccountDto> responseAccount = accountService.getById(1);
        Assertions.assertNotNull(responseAccount);
        StepVerifier.create(responseAccount).expectNext(accountsDtos.get(0)).verifyComplete();
    }


    @Test
    public void getNonExistingAccount() {
        when(template.selectOne(any(), any())).thenReturn(Mono.empty());
        Mono<AccountDto> responseAccount = accountService.getById(55);
        StepVerifier.create(responseAccount).verifyErrorMessage("Account with id 55 was not found!");
    }

    @Test
    public void deleteAccountById() {

        when(template.delete(any(), any())).thenReturn(Mono.just(1));
        Mono<Integer> deletedAccount = accountService.deleteAccountById(1);
        StepVerifier.create(deletedAccount).expectNext(1).verifyComplete();
    }

    @Test
    public void updateAccount() {
        AccountDto accountToUpdate = AccountDto.builder().id(accounts.get(0).getId()).build();

        when(template.update(any(Account.class))).thenReturn(Mono.just(accounts.get(0)));

        accountToUpdate.setAmount(new BigDecimal(700));
        accountToUpdate.setCurrency("EUR");
        accountToUpdate.setIssuedAt(LocalDate.of(2021, Month.JANUARY, 21));
        accountToUpdate.setIban("asfgsdfh");
        accountToUpdate.setCustomerId(25);

        Mono<AccountDto> updated = accountService.updateAccount(accountToUpdate);
        StepVerifier.create(updated).expectNext(accountsDtos.get(0)).verifyComplete();
    }


    @Test
    public void createNewAccount() {
        AccountDto newAccountInfo = AccountDto.builder().customerId(1).currency("EUR").iban("GB82WEST12345698765432").amount(new BigDecimal("20.0")).issuedAt(LocalDate.of(2022, Month.MAY, 7)).build();
        when(template.insert(Account.class)).thenReturn(new ReactiveInsertOperation.ReactiveInsert<Account>() {
            @Override
            public ReactiveInsertOperation.TerminatingInsert<Account> into(SqlIdentifier table) {
                return null;
            }

            @Override
            public Mono<Account> using(Account object) {
                return Mono.just(accounts.get(0));
            }
        });

        when(template.selectOne(any(), any())).thenReturn(Mono.just(Customer.builder().id(1).build()));

        Mono<AccountDto> responseAccountDto = accountService.createAccount(newAccountInfo);

        responseAccountDto.subscribe(res -> {
            assertEquals(1, res.getId());
            assertEquals(newAccountInfo.getIban(), res.getIban());
            assertEquals(newAccountInfo.getAmount(), res.getAmount());
            assertEquals(newAccountInfo.getCurrency(), res.getCurrency());
            assertEquals(newAccountInfo.getCustomerId(), res.getCustomerId());
            assertEquals(newAccountInfo.getIssuedAt(), res.getIssuedAt());
        });
    }

    @Test
    public void createNewAccountNonExistingCustomer() {
        AccountDto newAccountInfo = AccountDto.builder().customerId(1).currency("EUR").iban("GB82WEST12345698765432").amount(new BigDecimal("20.0")).issuedAt(LocalDate.of(2022, Month.MAY, 7)).build();
        when(template.selectOne(any(), any())).thenReturn(Mono.empty());

        Mono<AccountDto> responseAccountDto = accountService.createAccount(newAccountInfo);

        StepVerifier.create(responseAccountDto)
                .expectErrorMessage("Customer with id 1 was not found!")
                .verify();
    }

    @Test
    public void getAccountsForCustomer() {
        when(template.selectOne(any(), eq(Customer.class))).thenReturn(Mono.just(Customer.builder().id(1).build()));
        when(template.select(any(), eq(Account.class))).thenReturn(Flux.fromIterable(accounts.subList(0, 2)));
        Flux<AccountDto> accountsForCustomer = accountService.getAccountsForCustomer(1);

        StepVerifier.create(accountsForCustomer)
                .expectNext(accountsDtos.get(0), accountsDtos.get(1))
                .verifyComplete();
    }

    @Test
    public void getAccountsForNonExistingCustomer() {
        when(template.selectOne(any(), eq(Customer.class))).thenReturn(Mono.empty());
        Flux<AccountDto> accountsForCustomer = accountService.getAccountsForCustomer(14);
        StepVerifier.create(accountsForCustomer).verifyErrorMessage("Customer with id 14 was not found!");
    }

    @Test
    public void testUpdatePatch() {
        AccountDto finalAccount = AccountDto.builder().id(1).customerId(1).currency("DOLLAR").iban("GB82WEST12345698765432").amount(new BigDecimal("20.0")).issuedAt(LocalDate.of(2022, Month.MAY, 7)).build();
        AccountDto dto = AccountDto.builder().id(1).currency("DOLLAR").build();

        when(template.selectOne(any(), eq(Account.class))).thenReturn(Mono.just(accounts.get(0)));
        when(template.update(accounts.get(0))).thenReturn(Mono.just(accounts.get(0)));

        Mono<AccountDto> updated = accountService.updateAccountPatch(dto);

        updated.subscribe(val ->
                StepVerifier.create(updated).expectNext(finalAccount).verifyComplete()
        );
    }

    @Test
    public void testUpdateFullPatch() {
        AccountDto finalAccount = AccountDto.builder().id(1).customerId(15).currency("RON").iban("GB82WEST12345698760000").amount(new BigDecimal("250.0")).issuedAt(LocalDate.of(2021, Month.MAY, 7)).build();
        AccountDto dto = AccountDto.builder().id(1).customerId(15).currency("RON").iban("GB82WEST12345698760000").amount(new BigDecimal("250.0")).issuedAt(LocalDate.of(2021, Month.MAY, 7)).build();

        when(template.selectOne(any(), eq(Account.class))).thenReturn(Mono.just(accounts.get(0)));
        when(template.update(accounts.get(0))).thenReturn(Mono.just(accounts.get(0)));

        Mono<AccountDto> updated = accountService.updateAccountPatch(dto);


        updated.subscribe(val ->
                StepVerifier.create(updated).expectNext(finalAccount).verifyComplete()
        );
    }
}
