public class Zoo{
	int numOfAnimals;
	String name;
	Cat cat;
	public Zoo(){
		numOfAnimals=0;
		cat=new Cat("British Shorthair");
	}
	
	public Zoo(int num,String name,Cat c){
		this.numOfAnimals=num;
		this.name=name;
		cat=c;
	}

	public void addAnimals(int num){
		numOfAnimals+=num;
	}

	public void addCats(int num){
		cat.increaseNum(num);
		numOfAnimals+=num;
	}

	public int getNumOfAnimals(){
		return numOfAnimals;
	}

	public void feedCat(String food){
		System.out.println("one visitor is feeding cat with "+food);
		cat.eats(food);
	}
}

