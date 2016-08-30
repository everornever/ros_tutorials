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

	public void increaseNum(){
		this.num+=1;
	}

	public void eats(String food){
		System.out.println("I am eating "+food);
	}
}
