#version 150 core

// model view projection matrix
uniform mat4 uMvp;

// input attributes
in vec4 aVertex;
in vec2 aUV;

// output attributes
out vec2 vUV;

void main() {
  gl_Position = uMvp * vec4(aVertex.x, aVertex.y, 0.0, 1.0);
  vUV = aUV;
}
