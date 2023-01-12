import java.util.ArrayList;
import java.util.Date;

abstract class Investor {
    protected static String date;
    protected String firstName;
    protected String lastName;
    protected String username;
    protected String password;
    protected double money;
    protected double spentMoney;
    protected double addedMoney;
    protected int numTransactions;
    protected int stocksInPortfolio;
    protected ArrayList<Transaction> transactions;
    protected ArrayList<OwnedStock> portfolio;
    protected API api;

    public Investor(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        api = new API();
        date = java.time.LocalDate.now().toString();
    }

    // Abstract method that takes in ticker symbol of a stock and the quantity. The
    // method will add that quantity of stock to the investor's portfolio, and
    // remove the money spent. Creates a transaction object in the process.
    public abstract boolean buyStock(String ticker, long quantity);

    // Abstract method that takes in an array of tickers. Also takes in a double representing money the user wants to spend. The method will print out the combination of stocks, from the list, that will spend as much money as possible without going over the limit.
    public abstract boolean buyMax(ArrayList<String> tickers, double money);

    // Helper method for the buyMax method. Uses recursion.
    protected ArrayList<String> permute(ArrayList<String> tickers, double money, ArrayList<String> bought, double moneySpent) {
        ArrayList<String> bestCombo = bought;
        for (int i = 0; i < tickers.size(); i++) {
            ArrayList<String> newArray = bought;
            newArray.add(tickers.get(i));
            if (calcValueOfArray(newArray) < money) {
                api.setSymbol(tickers.get(i));
                ArrayList<String> potentialBestCombo = permute(tickers, money, newArray, moneySpent + api.getPrice());
                if (calcValueOfArray(potentialBestCombo) > calcValueOfArray(bestCombo)) {
                    bestCombo = potentialBestCombo;
                }
            } else if (calcValueOfArray(newArray == money)) {
                return newArray;
            }
        }
        return bestCombo;
    }

    // Helper method for the buyMax method. Returns value of all stocks in a string array.
    protected double calcValueOfArray(ArrayList<String> array) {
        double value = 0;
        for (int i = 0; i < array.size(); i++) {
            api.setSymbol(array.get(i));
            value += api.getPrice();
        }
        return value;
    }

    // // Helper method for the buyMax method. Returns whether a string exists in the arraylist of strings.
    // protected boolean existsInArray(ArrayList<String> array, String string) {
    //     for (int i = 0; i < array.size(); i++) {
    //         if (array.get(i).equals(string)) {
    //             return true;
    //         }
    //     }
    //     return false;
    // }

    // Method that takes in ticker symbol of a stock and the quantity. The
    // method will remove that quantity of stock from the investor's portfolio, and
    // add the money received. Creates a transaction object in the process.
    public boolean sellStock(String ticker, long quantity) {
        for (int i = 0; i < stocksInPortfolio; i++) {
            if (portfolio.get(i).getTicker().equals(ticker)) {
                if (portfolio.get(i).getQuantity() >= quantity) {
                    api.setSymbol(ticker);
                    Sell sell = new Sell(date, ticker, quantity, Double.parseDouble(api.getPrice()));
                    portfolio.get(i).subtractQuantity(quantity);
                    money = money + sell.costOfTransaction();
                    transactions.add(sell);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    // Gets the index of the specified ticker if it exists. Returns -1 otherwise
    public int getTickerIndex (String ticker) {
      for (int i = 0; i < stocksInPortfolio; i++) {
        if (portfolio.get(i).getTicker().equals(ticker)) {
          return i;
        }
      }
      return -1;
    }

    // Prints transaction history
    public void printTransactionHistory() {
        if (numTransactions == 0) {
            System.out.println("You have not made any transactions.");
            return;
        }
        for (int i = 0; i < numTransactions; i++) {
            System.out.println(transactions.get(i));
            System.out.println();
        }
    }

    // Returns a double which represents the net worth of the investor. Is
    // calculated by finding value of all stocks and adding money onto that.
    public double getNetWorth() {
        double netWorth = 0;

        for (int i = 0; i < stocksInPortfolio; i++) {
            String ticker = portfolio.get(i).getTicker();
            Long quantity = portfolio.get(i).getQuantity();
            api.setSymbol(ticker);
            netWorth += quantity * api.getPrice();
        }

        netWorth += money;

        return netWorth;
    }

    // Sells every stock the user owns. Credits the money to the account
    // accordingly.
    public boolean sellAll() {
        for (int i = 0; i < stocksInPortfolio; i++) {
            String ticker = portfolio.get(i).getTicker();
            Long quantity = portfolio.get(i).getTicker();
            sellStock(ticker, quantity);
        }
        return true;
    }

    // Prints out all the stocks and quantity owned of all stocks.
    public void viewPortfolio() {
        if (stocksInPortfolio == 0) {
            System.out.println("You do not have any stocks in your portfolio.");
            return;
        }
        for (int i = 0; i < stocksInPortfolio; i++) {
            System.out.println(portfolio.get(i));
            System.out.println();
        }
    }

    // Adds money to the investor's account
    public boolean addMoney(double money) {
        this.money += money;
        addedMoney += money;
        return true;
    }

    // Returns the lifetime profit of the user
    public double calculateProfit() {
        return getNetWorth() - addedMoney;
    }

    // Subtracts money from the user
    public boolean withdrawMoney(double money) {
        if (money > this.money) {
            return false;
        } else {
            this.money -= money;
            return true;
        }
    }

    // Sort portfolio by price

    // Returns all information of the investor in a clean and organized manner
    public String toString() {
        String output = "First Name: " + firstName + "\n";
        output += "Last Name: " + lastName + "\n";
        output += "Money: " + money + "\n";
        output += "Money Spent: " + spentMoney + "\n";
        output += "Stocks In Portfolio: " + stocksInPortfolio + "\n";
        output += "Lifetime Profit: " + calculateProfit();
        return output;
    }
}