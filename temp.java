public class temp {
	public static void main(String[] args){
int x = 4838;
int result = 0;

while(x > -4)
{
result = result * 10 + x % 10;
x /= 10;
}
System.out.println(result);
}
}