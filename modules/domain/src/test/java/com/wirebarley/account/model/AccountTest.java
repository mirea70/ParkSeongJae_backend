package com.wirebarley.account.model;

import com.wirebarley.error.exception.DomainException;
import com.wirebarley.error.info.AccountErrorInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    @DisplayName("계좌는 생성될 때, 상태: ACTIVE / 잔액: 0 / 삭제일시 null로 초기화된다.")
    void create_initializes_defaults() {
        // given
        AccountStatus initStatus = AccountStatus.ACTIVE;
        long initBalanceVal = 0;
        LocalDateTime initClosedAt = null;

        Long id = 1L;
        Long customerId = 10L;
        String bankCode = BankCode.KOOKMIN.getCode();
        String accountNumber = "009123456789";
        LocalDateTime now = LocalDateTime.now();

        // when
        Account account = Account.createNew(id, customerId, bankCode, accountNumber, now);

        // then
        assertThat(account.getId().getValue()).isEqualTo(id);
        assertThat(account.getCustomerId().getValue()).isEqualTo(customerId);

        BankInfo bankInfo = account.getBankInfo();
        assertThat(bankInfo.getBankCode().getCode()).isEqualTo(bankCode);
        assertThat(bankInfo.getAccountNumber().getValue()).isEqualTo(accountNumber);

        assertThat(account.getStatus()).isEqualTo(initStatus);
        assertThat(account.getBalance().getValue()).isEqualTo(initBalanceVal);
        assertThat(account.getClosedAt()).isEqualTo(initClosedAt);

        assertThat(account.getCreatedAt()).isEqualTo(now);
        assertThat(account.getUpdatedAt()).isEqualTo(now);
        assertThat(account.getUpdatedAt()).isAfterOrEqualTo(account.getCreatedAt());
    }

    @DisplayName("계좌는 해지 시, 해지상태로 변경되며 해지날짜가 기록된다.")
    @Test
    void close() {
        // given
        LocalDateTime now = LocalDateTime.now();

        Account account = Account.createNew(
                1L,
                2L,
                BankCode.IBK.getCode(),
                "123123112132",
                now
                );

        // when
        account.close(now);

        // then
        assertThat(account.getStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(account.getClosedAt()).isEqualTo(now);
    }

    @Nested
    @DisplayName("입금 테스트")
    class Deposit {

        @DisplayName("계좌에 입금하면 그 양만큼 현재잔액이 증가하며, 계좌 거래를 생성한다.")
        @Test
        void deposit() {
            // given
            LocalDateTime now = LocalDateTime.now();

            Account account = Account.createNew(
                    2L,
                    2L,
                    "039",
                    "123123123123123",
                    now
            );

            Long beforeBalance = account.getBalance().getValue();
            Long amount = 10000L;
            LocalDateTime afterAt = LocalDateTime.now().plusDays(1);
            Long accountTransactionId = 1L;

            // when
            AccountTransaction accountTransaction = account.deposit(amount, afterAt, accountTransactionId);

            // then
            Long afterBalance = account.getBalance().getValue();
            assertThat(afterBalance).isEqualTo(beforeBalance + amount);
            assertThat(account.getUpdatedAt()).isEqualTo(afterAt);
            assertThat(accountTransaction).isNotNull();
            assertThat(accountTransaction.getId().getValue()).isEqualTo(accountTransactionId);
        }

        @DisplayName("계좌에 입금할 금액은 양의 정수여야한다.")
        @ParameterizedTest
        @MethodSource("depositAmountCases")
        void depositFailByAmount(Long amount, AccountErrorInfo errorInfo) {
            // given
            LocalDateTime now = LocalDateTime.now();

            Account account = Account.createNew(
                    2L,
                    2L,
                    "039",
                    "123123123123123",
                    now
            );

            // when // then
            assertThatThrownBy(() -> account.deposit(amount, now.plusDays(1), 1L))
                    .isInstanceOf(DomainException.class)
                    .extracting("errorInfo")
                    .isEqualTo(errorInfo);
        }

        private static Stream<Arguments> depositAmountCases() {
            return Stream.of(
                    Arguments.of(
                            null, AccountErrorInfo.DEPOSIT_NOT_EXIST
                    ),
                    Arguments.of(
                            -1000L, AccountErrorInfo.DEPOSIT_NOT_POSITIVE
                    ),
                    Arguments.of(
                            0L, AccountErrorInfo.DEPOSIT_NOT_POSITIVE
                    )
            );
        }

        @DisplayName("해지된 계좌에는 입금할 수 없다.")
        @Test
        void depositFailWhenClosed() {
            // given
            LocalDateTime now = LocalDateTime.now();

            Account account = Account.createNew(
                    2L,
                    2L,
                    "039",
                    "123123123123123",
                    now
            );
            account.close(now.plusDays(1));

            Long amount = 10000L;

            // when // then
            assertThatThrownBy(() -> account.deposit(amount, now.plusDays(1), 1L))
                    .isInstanceOf(DomainException.class)
                    .extracting("errorInfo")
                    .isEqualTo(AccountErrorInfo.CLOSED);
        }
    }

    @Nested
    @DisplayName("출금 테스트")
    class Withdraw {

        @DisplayName("계좌에서 출금하면 그 양만큼 현재잔액이 감소하며, 계좌 거래를 생성한다.")
        @Test
        void withdraw() {
            // given
            LocalDateTime now = LocalDateTime.now();

            Account account = Account.fromOutside(
                    2L,
                    2L,
                    "039",
                    "123123123123123",
                    "ACTIVE",
                    20000L,
                    now,
                    now,
                    null
            );

            Long beforeBalance = account.getBalance().getValue();
            Long amount = 10000L;
            LocalDateTime afterAt = LocalDateTime.now().plusDays(1);
            Long accountTransactionId = 1L;
            Long dailyWithdrawAmount = 0L;

            // when
            AccountTransaction accountTransaction = account.withdraw(amount, afterAt, accountTransactionId, dailyWithdrawAmount);

            // then
            Long afterBalance = account.getBalance().getValue();
            assertThat(afterBalance).isEqualTo(beforeBalance - amount);
            assertThat(account.getUpdatedAt()).isEqualTo(afterAt);
            assertThat(accountTransaction).isNotNull();
            assertThat(accountTransaction.getId().getValue()).isEqualTo(accountTransactionId);
        }

        @DisplayName("계좌에 출금할 금액은 양의 정수여야한다.")
        @ParameterizedTest
        @MethodSource("withdrawAmountCases")
        void withdrawFailByAmount(Long amount, AccountErrorInfo errorInfo) {
            // given
            LocalDateTime now = LocalDateTime.now();

            Account account = Account.createNew(
                    2L,
                    2L,
                    "039",
                    "123123123123123",
                    now
            );

            // when // then
            assertThatThrownBy(() -> account.withdraw(amount, now.plusDays(1), 1L, 0L))
                    .isInstanceOf(DomainException.class)
                    .extracting("errorInfo")
                    .isEqualTo(errorInfo);
        }

        private static Stream<Arguments> withdrawAmountCases() {
            return Stream.of(
                    Arguments.of(
                            null, AccountErrorInfo.WITHDRAW_NOT_EXIST
                    ),
                    Arguments.of(
                            -1000L, AccountErrorInfo.WITHDRAW_NOT_POSITIVE
                    ),
                    Arguments.of(
                            0L, AccountErrorInfo.WITHDRAW_NOT_POSITIVE
                    )
            );
        }


        @DisplayName("해지된 계좌에는 출금할 수 없다.")
        @Test
        void withdrawFailWhenClosed() {
            // given
            LocalDateTime now = LocalDateTime.now();

                Account account = Account.createNew(
                        2L,
                        2L,
                        "039",
                        "123123123123123",
                        now
                );
                account.close(now.plusDays(1));

                Long amount = 10000L;
                Long dalyWithDrawAmount = 0L;

                // when // then
                assertThatThrownBy(() -> account.withdraw(amount, now.plusDays(1), 1L, dalyWithDrawAmount))
                        .isInstanceOf(DomainException.class)
                        .extracting("errorInfo")
                        .isEqualTo(AccountErrorInfo.CLOSED);
            }

            @DisplayName("출금 일일한도액을 초과해서 출금할 수 없다.")
            @Test
            void withdrawFailWhenOverDailyLimit() {
                // given
                LocalDateTime now = LocalDateTime.now();

                Account account = Account.fromOutside(
                        2L,
                        2L,
                        "039",
                        "123123123123123",
                        "ACTIVE",
                        20000L,
                        now,
                        now,
                        null
                );

                Long amount = 10000L;
                Long dalyWithDrawAmount = 999999L;

                // when // then
                assertThatThrownBy(() -> account.withdraw(amount, now.plusDays(1), 1L, dalyWithDrawAmount))
                        .isInstanceOf(DomainException.class)
                        .extracting("errorInfo")
                        .isEqualTo(AccountErrorInfo.OVER_WITHDRAW_LIMIT);
            }

            @DisplayName("계좌의 잔액이 부족하면 출금할 수 없다.")
            @Test
            void withdrawFailWhenLackBalance() {
                // given
                LocalDateTime now = LocalDateTime.now();

                Account account = Account.createNew(
                        2L,
                        2L,
                        "039",
                        "123123123123123",
                        now
                );

                Long amount = 10000L;
                Long dalyWithDrawAmount = 0L;

                // when // then
                assertThatThrownBy(() -> account.withdraw(amount, now.plusDays(1), 1L, dalyWithDrawAmount))
                        .isInstanceOf(DomainException.class)
                        .extracting("errorInfo")
                        .isEqualTo(AccountErrorInfo.LACK_BALANCE);
            }
    }
}
