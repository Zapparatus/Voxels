

package com.voxels;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import com.voxels.graphics.GraphicsRoutines;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Voxels {
	private boolean[] keys = new boolean[65536];
	private float zPosition = -100;
	private float xPosition = 0;
	private int cube;
	private int texture;
	private long window = 0;
	
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final int speed = 1;
	
	public Voxels() {
		
	}
	
	public void run() {
		try {
			// Initialize OpenGL
			initOpenGL();
			
			// Initialize game
			init();
			
			// Start the game loop
			loop();
			
			// Free the callback routines for the window
			glfwFreeCallbacks(window);
			
			// Destroy the window
			glfwDestroyWindow(window);
		} catch (Exception e) {
			// Print out the message
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		} finally {
			// Terminate GLFW
			glfwTerminate();
			
			// Free the error callback
			glfwSetErrorCallback(null).free();
		}
	}
	
	public void initOpenGL() throws Exception {
		// Set System.err as the output for error messages
		GLFWErrorCallback.createPrint(System.err).set();
		
		// Initialize GLFW. (Most GLFW require this)
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		// Set the window hints to default
		glfwDefaultWindowHints();
		
		// Make the window hidden on creation
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		
		// Make the window static size
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		
		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "Voxels", NULL, NULL);
		
		// Make sure the window was created
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}
		
		// Set handleKey as the function to handle key presses
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			handleKey(window, key, scancode, action, mods);
		});
		
		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		
		// Position the window in the center of the primary monitor
		glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2);
		
		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		
		// Enable v-sync
		glfwSwapInterval(1);
		
		// Make the window visible
		glfwShowWindow(window);
		
		// Create new capabilities for the new context
		GL.createCapabilities();
		
		// Change to projection matrix mode
		glMatrixMode(GL_PROJECTION);
		
		// Load the identity matrix
		glLoadIdentity();
		
		// Set up the projection matrix
		GraphicsRoutines.gluPerspective(45.0f, 800f/600f, 0.1f, 1000f);
		
		// Change to model-view matrix mode
		glMatrixMode(GL_MODELVIEW);
		
		// Load the identity matrix
		glLoadIdentity();
		
		// Enable depth testing
		glEnable(GL_DEPTH_TEST);
		
		// Set the depth testing function to less than or equal
		glDepthFunc(GL_LEQUAL);
		
		// Use the highest quality option for implementation of perspective correction
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		
		// Load the identity matrix
		glLoadIdentity();
		
		// Change how polygons are rasterized
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}
	
	public void init() throws Exception {
		// Load the sprite sheet here
		texture = GraphicsRoutines.loadImage("/res/Blocks.png");
		
		// Create a display list for the dirt cube
		cube = glGenLists(1);
		
		// Let OpenGL know we are compiling a list
		glNewList(cube, GL_COMPILE);
		
		// Perform commands for the list
		renderCube();
		
		// End the list
		glEndList();
	}
	
	public void loop() {
		// Make the OpenGL bindings available for use (LWJGL required)
		//GL.createCapabilities();
		
		// Enable 2d textures
		glEnable(GL_TEXTURE_2D);
		
		// Set the clear color
		glClearColor(0.0f, 1.0f, 1.0f, 0.0f);
		
		// Do a render loop until the window is to be closed
		while (!glfwWindowShouldClose(window)) {
			// Render the scene
			render();
			
			// Check for events and act on them
			update();
		}
	}
	
	public void render() {
		// Clear the frame buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Set the viewport and load the identity matrix
		glViewport(0, 0, 800, 600);
		glLoadIdentity();
		
		// Move to the cube's position
		glTranslatef(xPosition, 0, zPosition);
		
		// Draw a cube
		glCallList(cube);
		
		// Swap the buffers
		glfwSwapBuffers(window);
	}
	
	// Render a cube
	private void renderCube() {
		// Bind the sprite sheet
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		
		// Begin drawing GL_QUADS
		glBegin(GL_QUADS);
		
		// Top left vertex
		glTexCoord2f(0.25f, 0);
		glVertex3f(-1, 1, 1);
		
		// Top right vertex
		glTexCoord2f(0.5f, 0);
		glVertex3f(1, 1, 1);
		
		// Bottom right vertex
		glTexCoord2f(0.5f, 0.25f);
		glVertex3f(1, -1, 1);
		
		// Bottom left vertex
		glTexCoord2f(0.25f, 0.25f);
		glVertex3f(-1, -1, 1);
		
		// Stop drawing GL_QUADS
		glEnd();
		
		// Stop using the sprite sheet texture
		GL11.glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void update() {
		// Poll for any window events
		glfwPollEvents();

		// Move the cube based on WASD
		if (keys[GLFW_KEY_W]) {
			zPosition += speed;
		}
		
		if (keys[GLFW_KEY_S]) {
			zPosition -= speed;
		}
		
		if (keys[GLFW_KEY_A]) {
			xPosition += speed;
		}
		
		if (keys[GLFW_KEY_D]) {
			xPosition -= speed;
		}
	}
	
	public void handleKey(long window, int key, int scancode, int action, int mods) {
		// If the user wishes to exit, signal GLFW to do so
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
			glfwSetWindowShouldClose(window, true);
		}
		
		// Set the corresponding entry in keys to the changed key state
		keys[key] = action != GLFW_RELEASE;
	}
	
	public static void main(String[] args) {
		// Start the game
		new Voxels().run();
	}
}
