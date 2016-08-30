public class Cat{
	String species;
	int num;
	public Cat(String species){
		this.species=species;
		num=0;
	}		

	public String getSpecies(){
		return this.species;
	}

	public int getNum(){
		return this.num;
	}

	public void increaseNum(int num){
		this.num+=num;
	}

	public void eats(String food){
		System.out.println("I am eating "+food);
	}
}
