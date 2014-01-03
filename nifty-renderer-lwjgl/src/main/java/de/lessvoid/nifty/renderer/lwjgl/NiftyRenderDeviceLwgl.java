package de.lessvoid.nifty.renderer.lwjgl;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.nio.FloatBuffer;
import java.util.logging.Logger;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

import de.lessvoid.coregl.CoreFBO;
import de.lessvoid.coregl.CoreFactory;
import de.lessvoid.coregl.CoreRender;
import de.lessvoid.coregl.CoreShader;
import de.lessvoid.coregl.CoreTexture2D;
import de.lessvoid.coregl.CoreTexture2D.ColorFormat;
import de.lessvoid.coregl.CoreTexture2D.ResizeFilter;
import de.lessvoid.coregl.CoreTexture2D.Type;
import de.lessvoid.coregl.CoreVAO;
import de.lessvoid.coregl.CoreVBO;
import de.lessvoid.coregl.lwjgl.CoreFactoryLwjgl;
import de.lessvoid.math.Mat3;
import de.lessvoid.nifty.internal.math.Mat4;
import de.lessvoid.nifty.internal.math.MatrixFactory;
import de.lessvoid.nifty.spi.NiftyRenderDevice;
import de.lessvoid.nifty.spi.NiftyTexture;

public class NiftyRenderDeviceLwgl implements NiftyRenderDevice {
  private static final int VERTEX_SIZE = 5*6;
  private static final int MAX_QUADS = 2000;
  private static final int VBO_SIZE = MAX_QUADS * VERTEX_SIZE;

  private final Logger log = Logger.getLogger(NiftyRenderDeviceLwgl.class.getName());

  private final CoreFactory coreFactory;
  private final ShaderFactory shaderFactory;
  private final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
  private final CoreShader shader;
  private CoreVAO vao;
  private CoreVBO vbo;
  private final CoreFBO fbo;
  private final Mat4 mvp;
  private Mat4 mat = new Mat4();
  private int quadCount;

  public NiftyRenderDeviceLwgl() {
    coreFactory = new CoreFactoryLwjgl();
    shaderFactory = new ShaderFactory(coreFactory);
    mvp = MatrixFactory.createOrtho(0, getDisplayWidth(), getDisplayHeight(), 0);

    shader = coreFactory.newShaderWithVertexAttributes("aVertex", "aUVL");
    shader.vertexShader("de/lessvoid/nifty/renderer/lwjgl/texture.vs");
    shader.fragmentShader("de/lessvoid/nifty/renderer/lwjgl/texture.fs");
    shader.link();
    shader.activate();
    shader.setUniformi("uTexture", 0);

    fbo = coreFactory.createCoreFBO();
    fbo.bindFramebuffer();
    fbo.disable();

    vao = coreFactory.createVAO();
    vao.bind();

    vbo = coreFactory.createStream(new float[VBO_SIZE]);
    vbo.bind();

    vao.enableVertexAttributef(0, 2, 5, 0);
    vao.enableVertexAttributef(1, 3, 5, 2);

    vao.unbind();
  }

  private void addQuad(final FloatBuffer buffer, final int x, final int y, final int width, final int height, final int texIdx) {
    // first
    buffer.put(x);
    buffer.put(y);
    buffer.put(0.0f);
    buffer.put(0.0f);
    buffer.put(texIdx);

    buffer.put(x);
    buffer.put(y + height);
    buffer.put(0.0f);
    buffer.put(1.0f);
    buffer.put(texIdx);

    buffer.put(x + width);
    buffer.put(y);
    buffer.put(1.0f);
    buffer.put(0.0f);
    buffer.put(texIdx);

    // second
    buffer.put(x);
    buffer.put(y + height);
    buffer.put(0.0f);
    buffer.put(1.0f);
    buffer.put(texIdx);

    buffer.put(x + width);
    buffer.put(y);
    buffer.put(1.0f);
    buffer.put(0.0f);
    buffer.put(texIdx);

    buffer.put(x + width);
    buffer.put(y + height);
    buffer.put(1.0f);
    buffer.put(1.0f);
    buffer.put(texIdx);
  }

  @Override
  public int getDisplayWidth() {
   return 1024;
  }

  @Override
  public int getDisplayHeight() {
    return 768;
  }

  @Override
  public NiftyTexture createTexture(final int width, final int height) {
    return new NiftyTextureLwjgl(coreFactory, width, height);
  }

  @Override
  public void render(final NiftyTexture renderTarget, final Mat4 mat) {
    Mat4 m = mat;
    Mat4.mul(mvp, m, this.mat);
    addQuad(vbo.getBuffer(), 0, 0, renderTarget.getWidth(), renderTarget.getHeight(), 0);
    quadCount++;
    ((NiftyTextureLwjgl) renderTarget).bind();

    flush();
  }

  private void flush() {
    if (quadCount == 0) {
      return;
    }
    shader.activate();
    shader.setUniformMatrix4f("uMvp", mat.toBuffer());
    shader.setUniformf("uOffset", 0, 0, 0.f);

    vao.bind();

    vbo.bind();
    vbo.getBuffer().flip();
    vbo.send();

    coreFactory.getCoreRender().renderTriangles(6*quadCount);
    vao.unbind();
    vbo.getBuffer().clear();
    quadCount = 0;
  }

  @Override
  public void begin() {
//    glClearColor((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.f);
    glClearColor(0.f, 0.f, 0.f, 1.f);
    glClear(GL_COLOR_BUFFER_BIT);

    vbo.getBuffer().clear();
    quadCount = 0;
  }

  @Override
  public void end() {
    flush();
  }

  @Override
  public void beginStencil() {
  }

  @Override
  public void endStencil() {
  }

  @Override
  public void enableStencil() {
  }

  @Override
  public void disableStencil() {
  }
}
