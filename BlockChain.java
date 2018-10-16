import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.EnumMap;
import java.util.HashMap;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.io.UnsupportedEncodingException;
import java.io.FileWriter;

public class BlockChain {
    private ArrayList<Block> blockchain; //ArrayList representation of blockChain
    public final static BlockChainKeys[] KEYS = new BlockChainKeys[] {BlockChainKeys.INDEX, BlockChainKeys.TIMESTAMP, BlockChainKeys.SENDER, BlockChainKeys.RECEIVER, BlockChainKeys.AMOUNT,BlockChainKeys.NONCE,BlockChainKeys.HASH};
     /**
    *Creates and instance of BlockChain
     */
    public BlockChain(){
        this.blockchain = new ArrayList<>();
    } 
    public static void main(String args[]){
        BlockChain blockChainFromFile = fromFile("blockchain_sgagb074.txt");
        if ((blockChainFromFile.validateBlockChain())){
            boolean addNewBlock = true;
            while(addNewBlock){
                EnumMap<BlockChainKeys, String> userInputs = getTransactionFromUser(blockChainFromFile);
                //Once valid information is returned, a transaction block is made, followed by a block and that block is added to the chain
                Transaction newTransaction = new Transaction(userInputs.get(BlockChainKeys.SENDER), userInputs.get(BlockChainKeys.RECEIVER),Integer.parseInt(userInputs.get(BlockChainKeys.AMOUNT)));
                Block newBlock = new Block(newTransaction);
                //If there is a previousHash for the block, it is set and the hash of the new block is updated to
                //contain the previous hash in the toString method.
                if(blockChainFromFile.size() >= 2){
                    newBlock.setPreviousHash(blockChainFromFile.getBlockAt(blockChainFromFile.size()-1).getHash());
                    System.out.println("NumberOfTrials: "+newBlock.updateHash());
                }
                blockChainFromFile.add(newBlock);
                
                Scanner inputScanner = new Scanner(System.in);
                System.out.print("Transaction complete. Would you like to perform another transaction: ");
                String answer = inputScanner.nextLine();
                if(!(answer.toLowerCase().trim().equals("yes")))
                    addNewBlock = false;
                inputScanner = new Scanner(System.in);
            }   
        }
        else{
            System.out.println("Oops! Your BlockChain file is invalid.");

        }
        blockChainFromFile.toFile("blockchain_sgagb074.txt");
        System.out.println("Block chain file saved.");
    }
    /**
    A method that prompts user for a sender name, receiver name and an amount to send
    Once valid information is gathered, it is returned via an Enump Map containing all 3 values.
    @param blockChainFromFile the blockChain to retrieve transactiosn from
    @return the sender name, receiver name and amount to be sent.
     */
    public static EnumMap<BlockChainKeys,String> getTransactionFromUser(BlockChain blockChainFromFile){
        Scanner inputScanner = new Scanner(System.in);
        boolean validInput = false;
        String sender = "";
        String receiver = "";
        int amount = 0;
        while (!validInput){
            System.out.print("Enter the sender's username: ");
            sender = inputScanner.nextLine().trim();
            System.out.print("Enter the receiver's username: ");
            receiver = inputScanner.nextLine().trim();
            System.out.print("Enter the amount you would like to send to " + receiver +": ");
            try{
                amount = inputScanner.nextInt();
            }
            catch(InputMismatchException e){
                System.out.println("Oops! Please enter a valid integer.");
            }
            if((sender.equals(receiver))){
                System.out.println("Nice try, you can't send Bitcoins to yourself!");
            }
            else if(amount > blockChainFromFile.userBalance(sender)) {
                System.out.println("Sorry, you don't have enough Bitcoins to send. You current balance is: " + blockChainFromFile.userBalance(sender));
            }
            else{
                validInput = true;
            }
            System.out.print("\n");
            inputScanner = new Scanner(System.in);
        }
        EnumMap<BlockChainKeys,String> userInputs = new EnumMap<BlockChainKeys,String>(BlockChainKeys.class);
        userInputs.put(BlockChainKeys.SENDER,sender);
        userInputs.put(BlockChainKeys.RECEIVER,receiver);
        userInputs.put(BlockChainKeys.AMOUNT,Integer.toString(amount));
        return userInputs;
    }

    /**
    This method converts a file into a BlockChain 
    @param fileName The name of the file to be converted into a blockChain
    @return returns a BlockChain object created from the specified file
     */
    public static BlockChain fromFile(String fileName){
        File blockChainFile = new File(fileName);
        Scanner blockChainFileScanner;
        BlockChain newBlockChain = new BlockChain();
        //Tries to create a file scanner using the file info received
        try{
            blockChainFileScanner = new Scanner(blockChainFile);
        }
        catch(FileNotFoundException e){
            return new BlockChain();
        }
        // used to keep track of block attributes in from (range from 1-6)
        int blockAttributeIndex = -1;
        EnumMap<BlockChainKeys,String> blockHashMap = new EnumMap<BlockChainKeys,String>(BlockChainKeys.class); //A hashmap that represents block from a file
        while(blockChainFileScanner.hasNext()){
            blockAttributeIndex++;
            //Places the attribute into the hashMap representation of the block
            blockHashMap.put(KEYS[blockAttributeIndex],blockChainFileScanner.nextLine());
            if(blockAttributeIndex == 6){
                //Once all attributes are retrieved, a block is made from the block hash map and added to the blockChain
                newBlockChain.add(new Block(blockHashMap));
                blockHashMap = new EnumMap<BlockChainKeys,String>(BlockChainKeys.class);
                blockAttributeIndex = -1;
            }
        }
        
        blockChainFileScanner.close();
        return newBlockChain;
    }

    /**
    This method will update the a text file with an updated blockChain
    @param fileName The file to be updated
     */
    public void toFile(String fileName){
        Path filePath = Paths.get(fileName);
        ArrayList<String> arrayStringBlockChain = new ArrayList<>();
       
        for(int i = 0; i < blockchain.size(); i++){
            //Appends all block chain array lists to one list
            arrayStringBlockChain.addAll(fileRepresentationOfBlock(i));  
        } 
        try{
            //Overwrites the blockChain file, updating it with the new transactions
            Files.write(filePath,arrayStringBlockChain,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch(IOException e){
            System.out.println("Aborting: Error occurred when trying to save blockChain");
            return;
        }  
    }
    /**
    Determines whether the current block chani is valid
    @return true if the block chain is valid, false otherwise
     */
    public boolean validateBlockChain(){
        for(int i = 0; i < blockchain.size(); i++){
            Block currentBlock = blockchain.get(i);
            Transaction blockTransaction = currentBlock.getTransaction();
            //Validates that the hash value was created from the toString method
            try{
                if (!(Sha1.hash(currentBlock.toString()).equals(currentBlock.getHash()))){
                    return false;
                }
            }
            catch(UnsupportedEncodingException e){
                System.out.println("error validating block chain...");
            }
            if(!(currentBlock.getHash().substring(0,5).equals("00000")))
                return false;
            //Checks if a user has sent bitcoins to themselves
            if (blockTransaction.getSender().equals(blockTransaction.getReceiver()))
                return false;
            
            //Checks if the transaction is valid
            if(!(isValidTransaction(i)))
                return false;

            //Checks if the index of the block matches the its position in the block chain
            if(currentBlock.getIndex() != i)
                return false;
        
            //if the block is the 1st in the chain, checks if previous Hash begins with 00000
            //and checks all the other blocks have a previous hash that is equivalent to the hash of the block that came before it 
            if(i == 0 && !(currentBlock.getPreviousHash().equals("00000"))){
                return false;
            }
            else if (i != 0 && !(currentBlock.getPreviousHash().equals(blockchain.get(i-1).getHash()))){
                return false;
            }     
        }
        return true;
    }

    /**
    Checks if the transaction in the blockchain is valid by calculating the sender's balance 
    @param location the location of the transaction to validate
    @return true if the transaction is valid, false otherwise
     */
    private boolean isValidTransaction(int location){
        if(location - 1 == -1)
            return true;
        if(location < 0 || location >= blockchain.size())
            throw new IndexOutOfBoundsException("validateTransaction: location out of bounds");
        String sender = blockchain.get(location).getTransaction().getSender();
        int amount = blockchain.get(location).getTransaction().getAmount();
        int senderBalance = 0;
        //Looks at the transactions prior to the transaction at location to determine if the transaction at location was valid
        //(checks if the sender had enough money to send the money they did)
        for(int i = location-1; i >= 0; i--){
            Transaction transaction = blockchain.get(i).getTransaction();
            if(transaction.getReceiver().equals(sender)){
                senderBalance += transaction.getAmount();
            }
        }
        return senderBalance >= amount;
    }
    /**
    Adds the given block to the block chain
    @param blockToAdd the block to be added to the block chain
     */
     public void add(Block blockToAdd){
         //Sets the previous hash accordingly
        if(blockchain.size() == 0){
             blockToAdd.setPreviousHash("00000");
        }
        else{
             blockToAdd.setPreviousHash(blockchain.get(blockchain.size()-1).getHash());
        }
        blockToAdd.setIndex(blockchain.size());
        blockchain.add(blockToAdd);    
    }
    /**
    Calculate the user's account balance
    @param username the username of the user to search
    @return the account balance of the user
     */
    public int userBalance(String username){
        int balance = 0;
        for(int i = 0; i < blockchain.size(); i++){
            Transaction transaction = blockchain.get(i).getTransaction();
            if(transaction.getSender().equals(username)){
                balance -= transaction.getAmount();
            }
            if(transaction.getReceiver().equals(username)){
                balance += transaction.getAmount();
            }
        }
        return balance;
    }
    /**
    Creates a file formatted representation of a block. 
    @param location the location of the block to format 
    @return the formatted representation of the block
     */
    public ArrayList<String> fileRepresentationOfBlock(int location){
        ArrayList<String> fileRepresentation = new ArrayList<>();
        if(location < 0 || location >= blockchain.size())
            throw new IndexOutOfBoundsException("location not in range");
        Block block = blockchain.get(location);
        fileRepresentation.add(Integer.toString(block.getIndex()));
        fileRepresentation.add(Long.toString(block.getTimeStampInMillis()));
        fileRepresentation.add(block.getTransaction().getSender());
        fileRepresentation.add(block.getTransaction().getReceiver());
        fileRepresentation.add(Integer.toString(block.getTransaction().getAmount()));
        fileRepresentation.add(block.getNonce());
        fileRepresentation.add(block.getHash());
        return fileRepresentation;
    }
    /**
    Gives the block at the given location
    @param location the location to get the block from
    @return the block at location
     */
    public Block getBlockAt(int location){
        if(location < 0 || location >= blockchain.size())
            throw new IndexOutOfBoundsException("location not in range");
        return blockchain.get(location);
    }
    /**
    @return size of the blockchain
     */
    public int size(){
        return blockchain.size();
    }
}