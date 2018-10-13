import java.sql.Timestamp; 

public class Block {
    private int index; // the index of the block in the list private java.sql.Timestamp timestamp; // time at which transaction
    private Timestamp timestamp; //The time the transaction was processed
    private Transaction transaction; //the transaction stored in the block
    private String nonce; // random string (for proof of work) private String previousHash; // previous hash (set to "" in first block) //(in first block, set to string of zeroes of size of complexity "00000") private String hash; // hash of the block (hash of string obtained
    private String previousHash; //the hash of the block that came before this current block
    private String hash; //hash of the current block
    /**
    @return a String representation of a Block.
     */
    public String toString() {
        return timestamp.toString() + ":" + transaction.toString() + "." + nonce+ previousHash;
    }
}