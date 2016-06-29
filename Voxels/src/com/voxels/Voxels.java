

package com.voxels;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
 
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

@SuppressWarnings("unused")
public class Voxels {
	private long window = 0;
	
	public static int WIDTH = 800;
	public static int HEIGHT = 600;
	
	public Voxels() {
		
	}
	
	public void run() {
		try {
			initialize();
			loop();
			
			// Free the callback routines for the window
			glfwFreeCallbacks(window);
			
			// Destroy the window
			glfwDestroyWindow(window);
		} finally {
			// Terminate GLFW
			glfwTerminate();
			
			// Free the error callback
			glfwSetErrorCallback(null).free();
		}
	}
	
	public void initialize() {
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
	}
	
	public void loop() {
		// Make the OpenGL bindings available for use (LWJGL required)
		GL.createCapabilities();
		
		// Set the clear color
		glClearColor(0.0f, 1.0f, 1.0f, 0.0f);
		
		// Do a render loop until the window is to be closed
		while (!glfwWindowShouldClose(window)) {
			// Clear the frame buffer
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			// TODO - write rendering code here
			
			// Swap the buffers
			glfwSwapBuffers(window);
			
			// Poll for any window events
			glfwPollEvents();
		}
	}
	
	public void handleKey(long window, int key, int scancode, int action, int mods) {
		// Close the window when the escape key is released
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
			glfwSetWindowShouldClose(window, true);
		}
	}
	
	public static void main(String[] args) {
		// Start the game
		new Voxels().run();
	}
}
