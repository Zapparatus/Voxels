

package com.voxels;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.sun.javafx.geom.Vec3f;
import com.voxels.Block.BlockType;
import com.voxels.graphics.GraphicsRoutines;
import org.lwjgl.opengl.GL11;

public class Voxels {
	private boolean mouseLocked = false;
	private boolean[] keys = new boolean[65536];
	private Chunk spawn = null;
	private long window = 0;
	private Player player = null;
	
	private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int speed = 1;
	
	private Voxels() {
		
	}
	
	private void run() {
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
	
	private void initOpenGL() {
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
		glfwSetKeyCallback(window, this::handleKey);
		
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
		GraphicsRoutines.gluPerspective(80f, (float)WIDTH/HEIGHT, 1f, 10000f);
		
		// Change to model-view matrix mode
		glMatrixMode(GL_MODELVIEW);
		
		// Load the identity matrix
		glLoadIdentity();
		
		// Enable depth testing
		glEnable(GL_DEPTH_TEST);
		
		// Accept fragment if depth is less than or equal to another
		glDepthFunc(GL_LEQUAL);
		
		// Use the highest quality option for implementation of perspective correction
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		
		// Load the identity matrix
		glLoadIdentity();
		
		glEnable(GL_POLYGON_SMOOTH);
		
		// Change how polygons are rasterized
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
	}
	
	private void init() throws Exception {
		// Load all textures
		GraphicsRoutines.loadTextures();
		
		// Create a player
		player = new Player();
		
		// Create a test (spawn) Chunk
		spawn = new Chunk(new Vec3f(0, 0, 100));
		
		// Loop through the Blocks in the Chunk and create a test biome
		for (int x = 0; x < Chunk.CHUNK_SIZE; ++x) {
			for (int y = 0; y < Chunk.CHUNK_SIZE; ++y) {
				for (int z = 0; z < Chunk.CHUNK_SIZE; ++z) {
					if (x == 0) {
						// Spawn Sand to left
						spawn.setBlock(x, y, z, new Block(spawn, new Vec3f(spawn.getPosition().x - Block.DEFAULT_SIZE * x, spawn.getPosition().y + Block.DEFAULT_SIZE * y, spawn.getPosition().z - Block.DEFAULT_SIZE * z), BlockType.Sand));
					} else if (y == Chunk.CHUNK_SIZE - 1) {
						// Spawn Grass on top
						spawn.setBlock(x, y, z, new Block(spawn, new Vec3f(spawn.getPosition().x - Block.DEFAULT_SIZE * x, spawn.getPosition().y + Block.DEFAULT_SIZE * y, spawn.getPosition().z - Block.DEFAULT_SIZE * z), BlockType.Grass));
					} else {
						// Spawn Dirt below
						spawn.setBlock(x, y, z, new Block(spawn, new Vec3f(spawn.getPosition().x - Block.DEFAULT_SIZE * x, spawn.getPosition().y + Block.DEFAULT_SIZE * y, spawn.getPosition().z - Block.DEFAULT_SIZE * z), BlockType.Dirt));
					}
				}
			}
		}
		
		// Update the Chunk (update display list)
		spawn.update();
	}
	
	private void loop() {
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

	private Vec3f mulPos(Vec3f position, FloatBuffer fb) {
	    Vec3f output = new Vec3f();

	    output.x = position.x * fb.get(0) + position.y * fb.get(1) + position.z * fb.get(2);
        output.y = position.x * fb.get(4) + position.y * fb.get(5) + position.z * fb.get(6);
        output.z = position.x * fb.get(8) + position.y * fb.get(9) + position.z * fb.get(10);

	    return output;
    }

	private void render() {
		// Clear the frame buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		// Set the viewport and load the identity matrix
		glViewport(0, 0, 800, 600);
		glLoadIdentity();

        glPushMatrix();

        glTranslatef(0, 0, -10);

        // Begin drawing GL_QUADS
        glBegin(GL_QUADS);

        glColor3f(1.0f, 0.0f, 0.0f);

        // Top left vertex
        glVertex3f(-0.25f, 0.25f, 0.25f);

        // Top right vertex
        glVertex3f(0.25f, 0.25f, 0.25f);

        // Bottom right vertex
        glVertex3f(0.25f, -0.25f, 0.25f);

        // Bottom left vertex
        glVertex3f(-0.25f, -0.25f, 0.25f);

        glEnd();

        glPopMatrix();

		// Rotate everything according to the player's view
		glRotatef(player.getRotation().x, 1, 0, 0);
		glRotatef(player.getRotation().y, 0, 1, 0);

        float distance = 0;

        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, fb);

        while (distance < 20) {
            Vec3f vec = mulPos(new Vec3f(0, 0, distance), fb);

            vec.y = -vec.y;

            vec.add(player.getPosition());

            Block b = spawn.getBlock(vec);

            if (b != null && b.getBlockType() != BlockType.Air && glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS) {
                b.setBlockType(BlockType.Air);

                spawn.update();

                break;
            }

            distance += 0.5f;
        }

        glColor3f(1, 1, 1);

		// Move to the Chunk's position
		float xPosition = player.getPosition().x - spawn.getPosition().x;
		float yPosition = player.getPosition().y - spawn.getPosition().y;
		float zPosition = player.getPosition().z - spawn.getPosition().z;
		glTranslatef(xPosition, -yPosition, zPosition);

		// Render the spawn Chunk
		spawn.render();

		// Swap the buffers
		glfwSwapBuffers(window);
	}
	
	private void update() {
		// Poll for any window events
		glfwPollEvents();

		// Set aside a vector for the change in position
		Vec3f deltaPosition = new Vec3f();

		// Move the cube based on WASD
		if (keys[GLFW_KEY_W]) {
			deltaPosition.z += speed * Math.cos(player.getRotation().y / 180 * Math.PI);
			deltaPosition.x -= speed * Math.sin(player.getRotation().y / 180 * Math.PI);
		}
		
		if (keys[GLFW_KEY_S]) {
			deltaPosition.z -= speed * Math.cos(player.getRotation().y / 180 * Math.PI);
			deltaPosition.x += speed * Math.sin(player.getRotation().y / 180 * Math.PI);
		}
		
		if (keys[GLFW_KEY_A]) {
			deltaPosition.z += speed * Math.sin(player.getRotation().y / 180 * Math.PI);
			deltaPosition.x += speed * Math.cos(player.getRotation().y / 180 * Math.PI);
		}
		
		if (keys[GLFW_KEY_D]) {
			deltaPosition.z -= speed * Math.sin(player.getRotation().y / 180 * Math.PI);
			deltaPosition.x -= speed * Math.cos(player.getRotation().y / 180 * Math.PI);
		}
		
		if (keys[GLFW_KEY_SPACE]) {
			deltaPosition.y += speed;
		}
		
		if (keys[GLFW_KEY_LEFT_SHIFT]) {
			deltaPosition.y -= speed;
		}
		
		// Change the player's position accordingly
		float distance = 0;

        Block b = null;
        Vec3f tempPosition;

		while (distance < 100 && deltaPosition.length() > 0) {
            Vec3f tempDelta = new Vec3f(deltaPosition.x, deltaPosition.y, deltaPosition.z);

            tempDelta.normalize();

            tempDelta.mul(distance / 100 * deltaPosition.length());
            tempPosition = new Vec3f(player.getPosition().x, player.getPosition().y, player.getPosition().z);
            tempPosition.add(tempDelta);

            b = spawn.getBlock(tempPosition);

            if (b != null && b.getBlockType() != BlockType.Air) {
                break;
            }

            distance += 1f;
        }

        if (b == null || b.getBlockType() == BlockType.Air) {
            player.addPosition(deltaPosition);
        }
		
		// If the mouse clicks
		if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS && !mouseLocked) {
			// Set the cursor position to the center of the window
			glfwSetCursorPos(window, WIDTH / 2, HEIGHT / 2);
			
			// Lock the cursor
			mouseLocked = true;
			
			// Hide the cursor
			glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		}

		if (mouseLocked) {
			// Set aside buffers for the mouse data
			DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
			DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
			
			// Get the current mouse position
			glfwGetCursorPos(window, x, y);
			
			// Go back to the start of the buffers
			x.rewind();
			y.rewind();
			
			// Get the travel distance of the mouse
			float deltaX = (float)x.get() - WIDTH / 2;
			float deltaY = (float)y.get() - HEIGHT / 2;

			// Add rotation accordingly
			player.addRotation(new Vec3f(deltaY, deltaX, 0));

            if (player.getRotation().x >= 90) {
                player.setRotation(new Vec3f(90, player.getRotation().y, player.getRotation().z));
            }

            if (player.getRotation().x <= -90) {
                player.setRotation(new Vec3f(-90, player.getRotation().y, player.getRotation().z));
            }

            if (player.getRotation().y >= 360 || player.getRotation().y <= -360) {
                player.setRotation(new Vec3f(player.getRotation().x, player.getRotation().y % 360, player.getRotation().z));
            }

			// Set the cursor position back to the center of the window
			glfwSetCursorPos(window, WIDTH / 2, HEIGHT / 2);
		}
	}
	
	private void handleKey(long window, int key, int scancode, int action, int mods) {
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
