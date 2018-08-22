package norainu2;

import java.util.ArrayList;

public class ArrayListie<E> extends ArrayList<E> {

	public ArrayListie() {
		super();
	}

	//	public ArrayListie(Collection<E> c) {
	//		super(c);
	//	}
	//
	//	public ArrayListie(int i) {
	//		super(i);
	//	}

	public ArrayList<E> map2(mapProcedure<E> proc) {
		ArrayList<E> r_ = new ArrayList<>();
		for (int i = 0, len = super.size(); i < len; i++) {
			if (proc.process(super.get(i))) {
				r_.add(super.get(i));
			}
		}
		return r_;
	}
}
