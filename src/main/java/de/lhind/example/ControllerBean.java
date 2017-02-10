package de.lhind.example;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name="ControllerBean")
@ApplicationScoped
public class ControllerBean {
	
	private List<BasicBean> entryList;
	
	@PostConstruct
	public void createExampleList() {
		entryList = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			entryList.add(new BasicBean("Column 1 example " + i, "Column 2 example " + i, "Column 3 example " + i, i % 2 == 0));
		}
	}
	
	public List<BasicBean> getList() {
		return entryList;
	}
	
}
