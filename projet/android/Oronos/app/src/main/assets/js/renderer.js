// Check if WebGL is available; if not, use CanvasRenderer
function webglAvailable() {
    try {
        var canvas = document.createElement("canvas");
        return !!
            window.WebGLRenderingContext &&
            (canvas.getContext("webgl") ||
                canvas.getContext("experimental-webgl"));
    } catch (e) {
        return false;
    }
}

// Define the scene
var scene = new THREE.Scene();

// Define a perspective camera. Use small 'near' and 'far' planes since we don't need much space
var camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 10);

// Set renderer based on availability
var renderer = webglAvailable() ? new THREE.WebGLRenderer() : new THREE.CanvasRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);

// Append to document body
document.body.appendChild(renderer.domElement);

// Define perspective camera position
camera.position.z = 5;

// Define arrow shaft (cylinder) geometry
var arrowShaftGeometry = new THREE.CylinderGeometry(0.1, 0.1, 3, 8);
var arrowShaft = new THREE.Mesh(arrowShaftGeometry);

// Define arrow head (cone) geometry
var arrowHeadGeometry = new THREE.ConeGeometry(0.3, 1, 8);
var arrowHead = new THREE.Mesh(arrowHeadGeometry);
arrowHead.position.y = 1.95;

// Merge geometries to produce a single optimized mesh
var arrowGeometry = new THREE.Geometry();
arrowShaft.updateMatrix();
arrowGeometry.merge(arrowShaft.geometry, arrowShaft.matrix);
arrowHead.updateMatrix();
arrowGeometry.merge(arrowHead.geometry, arrowHead.matrix);

// Define material; basic is fine since we want to save on performance/battery
// Enable polygonOffset for adding wireframe without z-height conflicts
var material = new THREE.MeshBasicMaterial({
    color: 0x333333,
    polygonOffset: true,
    polygonOffsetFactor: 1,
    polygonOffsetUnits: 1
});

// Define final arrow mesh and add to scene.
var arrow = new THREE.Mesh(arrowGeometry, material);
scene.add(arrow);

// Define wireframe geometry/material/mesh, add it to the arrow
var wireGeometry = new THREE.EdgesGeometry(arrow.geometry);
var wireMaterial = new THREE.LineBasicMaterial({ color: 0xffffff, linewidth: 2 });
var wireframe = new THREE.LineSegments(wireGeometry, wireMaterial);
arrow.add(wireframe);

// Disable matrixAutoUpdate to save on performance/battery
arrow.matrixAutoUpdate = false;

// Get initial sensor orientation from Android parent
var rotationMatrixArray = [];
for (i = 0; i < 16; i++) {
    rotationMatrixArray.push(android.getRotationMatrixElement(i));
}
var rotationMatrix = new THREE.Matrix4();
rotationMatrix.fromArray(rotationMatrixArray);

// Define lastQuaternion and nextQuaternion from the rotationMatrix
// Since location might not be available yet, don't multiply the quaternion right now
// This will make the arrow point to the magnetic North while there is no location acquired
var lastQuaternion = new THREE.Quaternion();
lastQuaternion.setFromRotationMatrix(rotationMatrix);
var nextQuaternion = lastQuaternion.clone();

// We get 5 sensor updates per second, and render at 30 frames per second : 30/5 = 6
var frameSmoothing = 6;

// Animation function, called recursively
function animate() {

    // Render at 30 frames per second
    setTimeout(function () {
        requestAnimationFrame(animate);
    }, 1000 / 30)

    // Interpolate between quaternions to have smooth animation
    THREE.Quaternion.slerp(lastQuaternion, nextQuaternion, lastQuaternion, 1.0 / frameSmoothing);

    // Apply interpolated rotation quaternion to the arrow
    arrow.applyQuaternion(lastQuaternion);

    // Update the matrix for displaying it
    arrow.updateMatrix();

    // Decrement frame interpolation counter
    frameSmoothing--;

    // If it's at zero, we got new sensor info, so we need to create a new quaternion
    if (frameSmoothing === 0) {

        // Reset the counter
        frameSmoothing = 6;

        // Get the new sensor orientation from Android parent
        var rotationMatrixArray = [];
        for (i = 0; i < 16; i++) {
            rotationMatrixArray.push(android.getRotationMatrixElement(i));
        }
        var rotationMatrix = new THREE.Matrix4();
        rotationMatrix.fromArray(rotationMatrixArray);

        // Get Location data (unit vector towards target) from Android parent
        var unitVectorArray = [];
        for (i = 0; i < 3; i++) {
            unitVectorArray.push(android.getUnitVectorElement(i));
        }
        var locationVector = new THREE.Vector3();
        locationVector.fromArray(unitVectorArray);
        
        // Set the nextQuaternion (the last one will be interpolated towards it)
        nextQuaternion = new THREE.Quaternion();
        nextQuaternion.setFromRotationMatrix(rotationMatrix);

        // Multiply device orientation quaternion by location quaternion derived from location vector
        // This allows us to adjust arrow orientation based on both device orientation and location
        var locationQuaternion = new THREE.Quaternion();
        locationQuaternion.setFromUnitVectors(new THREE.Vector3(0,-1,0), locationVector);
        nextQuaternion.multiply(locationQuaternion);
    }

    // Render the scene
    renderer.render(scene, camera);

    // Reset arrow parameters for next frame (so we don't have scaling issues, etc.)
    arrow.position.set(0, 0, 0);
    arrow.rotation.set(0, 0, 0);
    arrow.scale.set(1, 1, 1);
    arrow.updateMatrix();
}

// Start animation
animate();