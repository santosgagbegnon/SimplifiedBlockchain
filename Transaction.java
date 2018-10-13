public class Transaction {
    private String sender; //The person sending the bitcoin(s) in the transaction 
    private String receiver; //The person receiving the bitcoin(s) in the rransaction
    private int amount; //The amount of bitcoins being sent by the sender to the receiver

    public Transaction(String sender, String receiver, int amount){
        /*Validates the parameters, if an invalid param is found, a custom message is printed 
        when the IllegalArgumentException is thrown.()*/
        if (sender == null)
            throw new IllegalArgumentException("Sender cannot be null.");
        if(receiver == null)
            throw new IllegalArgumentException("Receiver cannot be null.");
        if(amount <= 0)
            throw new IllegalArgumentException("Must send at least 1 Bitcoin.");
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }
    /**
    @return a String representation of a transaction
     */
    public String toString() {
        return sender + ":" + receiver + "=" + amount; 
    }
}