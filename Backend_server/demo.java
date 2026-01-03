class demo{
    private int data=40;
    void display(){
        System.out.println("Data: "+data);
    } 
}
class Main{
    public static void main(String args[]){
        demo obj=new demo();
        obj.display();
    }
}