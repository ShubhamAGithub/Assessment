import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class Assessment_Test {
    public static void main(String[] args) throws IOException {
        // Condition 1: Read data from the input CSV file
        String inputFile = "src/Data/account.csv";
        String transactionsOutputFile = "src/TransactionsFile/transactions.csv";

        try (
                FileReader reader = new FileReader(inputFile);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())
        ) {
            try (
                    FileWriter writer = new FileWriter(transactionsOutputFile);
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                            "TRANSACTION_ID","ACCOUNT_NUMBER", "TYPE", "DATE", "INITIAL_BALANCE", "CURRENT_BALANCE", "AMOUNT"
                    ))
            ) {
                for (CSVRecord record : csvParser) {
                    String accountNumber = record.get("ACCOUNT_NUMBER");
                    String type = record.get("TYPE");
                    Date openDate = new SimpleDateFormat("MM/dd/yyyy").parse(record.get("OPEN_DATE"));
                    BigDecimal initialBalance = new BigDecimal(record.get("INITIAL_BALANCE"));
                    BigDecimal currentBalance = new BigDecimal(record.get("CURRENT_BALANCE"));
                    BigDecimal loanAmount = new BigDecimal(record.get("LOAN_AMOUNT"));

                    // Calculate the number of transactions based on INITIAL_BALANCE
                    int numberOfTransactions = initialBalance.divide(loanAmount, 0, BigDecimal.ROUND_DOWN).intValue();

                    Calendar calendar = Calendar.getInstance();

                    for (int i = 0; i < numberOfTransactions; i++) {
                        Date currentDate = calendar.getTime();


                        BigDecimal updatedInitialBalance = initialBalance.subtract(currentBalance);
                        // Update current balances
                        currentBalance = currentBalance.add(loanAmount);

                        // Compare the current date with OPEN_DATE
                        if (currentDate.after(openDate)) {

                            if (updatedInitialBalance.compareTo(loanAmount) >= 0) {
                                // Update initial balances
                                BigDecimal finalInitialBalance = initialBalance.subtract(currentBalance);
                                // Create a transaction record
                                csvPrinter.printRecord(i,accountNumber, type, currentDate, finalInitialBalance,currentBalance, loanAmount);
                            }
                        }

                        // Move the current date one day back for the next iteration
                        calendar.add(Calendar.DAY_OF_MONTH, 0);
                    }
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
