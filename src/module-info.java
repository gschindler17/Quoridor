module quoridorModule {
	requires javafx.graphics; 
	requires javafx.controls;
	requires java.desktop;
	requires javafx.base;
	opens qPackage to javafx.graphics;
}