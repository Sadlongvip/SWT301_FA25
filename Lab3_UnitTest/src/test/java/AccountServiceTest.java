import NguyenThanhLong.example.AccountService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.platform.commons.io.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountServiceTest {
    static AccountService accountService;
    @BeforeAll
    static void initAll() {
        accountService = new AccountService();
    }

    @AfterAll
    static void cleanupAll() {
        accountService = null;
    }
    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    void testSignIn(String username, String password, String email, String expected) {
        boolean result = accountService.registerAccount(username, password, email);
        assertEquals(expected, String.valueOf(result), "Account was not successfully registered");
    }

}
