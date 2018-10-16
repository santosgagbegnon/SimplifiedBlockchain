import java.sql.Timestamp; 
import java.util.EnumMap;
import java.lang.Math;
import java.io.UnsupportedEncodingException;

public class Block {
    private int index; // the index of the block in the list private java.sql.Timestamp timestamp; // time at which transaction
    private Timestamp timestamp; //The time the transaction was processed
    private Transaction transaction; //the transaction stored in the block
    private String nonce = "00000"; // random string (for proof of work) private String previousHash; // previous hash (set to "" in first block) //(in first block, set to string of zeroes of size of complexity "00000") private String hash; // hash of the block (hash of string obtained
    private String previousHash; //the hash of the block that came before this current block
    private String hash; //hash of the current block
    /**
    Creates an instance of Block given a transaction
    @param transaction the transaction associated with the block being created
     */
    public Block(Transaction transaction){
        if (transaction == null)
            throw new IllegalArgumentException("Transaction cannot be null.");
        this.transaction = transaction;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.previousHash = "";
        this.index = 0;
        this.hash = "";
    }
    /**
    Constructs an instance of Block via a hash map representation of a block
    @param blockEnumMap a hash map representation of a block
     */
    public Block(EnumMap<BlockChainKeys,String> blockEnumMap){
        //Checks if every key required to make a block is inside the 
        for(int i =0; i < BlockChain.KEYS.length;i++){
            if(!(blockEnumMap.containsKey(BlockChain.KEYS[i])))
                throw new IllegalArgumentException("Could not parse given block hash map because values are missing");
         }
        //Retrieving infro from the EnumMap
        this.hash = blockEnumMap.get(BlockChainKeys.HASH);
        this.index = Integer.parseInt(blockEnumMap.get(BlockChainKeys.INDEX));
        this.nonce = blockEnumMap.get(BlockChainKeys.NONCE);
        this.timestamp = new Timestamp (Long.parseLong(blockEnumMap.get(BlockChainKeys.TIMESTAMP)));
        this.previousHash ="";
        String senderName = blockEnumMap.get(BlockChainKeys.SENDER);
        String receiverName = blockEnumMap.get(BlockChainKeys.RECEIVER);
        int transactionAmount =Integer.parseInt(blockEnumMap.get(BlockChainKeys.AMOUNT));
        this.transaction = new Transaction(senderName,receiverName,transactionAmount);
    }

    public String getHash(){
        return hash;
    }

    public String getPreviousHash(){
        return previousHash;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }

    public Transaction getTransaction(){
        return transaction;
    }

    public long getTimeStampInMillis(){
        return timestamp.getTime();
    }

    public String getNonce(){
        return nonce;
    }
    /**
    This method sets the hash of the current block. This is done by creating a random string (max length = 9) from ascii characters 
    once the proper nonce has been found and the hash begins with 5 zeros, the nonce and hash are updated.
     @return the number of trials required to achieve a valid hash
     */
    public int updateHash(){
        int numberOfTrials = 0;
        String currentHash = "";
        try{
            currentHash = Sha1.hash(toString());
        }
        catch(UnsupportedEncodingException e){
            return numberOfTrials;
        }
        if(currentHash.substring(0,5).equals("00000"))
            return numberOfTrials;

        String generatedHash = "aaaaa"; //random starting point
        while(!(generatedHash.substring(0,5).equals("00000"))){
            numberOfTrials ++;
            //Ensures the nonce length does not become too long
            if(nonce.length() > 8)
                nonce = nonce.substring(4,8);
            int randomAsciiCode = (int)(Math.random() * 127 + 33);
            this.nonce = nonce + Character.toString((char) randomAsciiCode);
            this.nonce = nonce.replace("\n","").replace("\t",""); 
            try {
                generatedHash = Sha1.hash(toString());
            }
            catch(UnsupportedEncodingException e){
                return 0;
            }
        }
        this.hash = generatedHash;
        return numberOfTrials;
    }

    public void setPreviousHash(String newPreviousHash){
        if (newPreviousHash == null)
            throw new IllegalArgumentException("previous hash cannot be null");
        this.previousHash = newPreviousHash;
    }

    /**
    @return a String representation of a Block.
     */
    public String toString() {
        return timestamp.toString() + ":" + transaction.toString() + "." + nonce+ previousHash;
    }
}