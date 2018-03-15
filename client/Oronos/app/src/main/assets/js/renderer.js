alert("working 2!");
var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 10);
var renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

camera.position.z = 5;

var arrowShaftGeometry = new THREE.CylinderGeometry(0.1, 0.1, 4, 8);
var arrowShaft = new THREE.Mesh(arrowShaftGeometry);

var arrowHeadGeometry = new THREE.ConeGeometry(0.3, 1, 8);
var arrowHead = new THREE.Mesh(arrowHeadGeometry);
arrowHead.position.y = 2.;

var arrowGeometry = new THREE.Geometry();

arrowShaft.updateMatrix();
arrowGeometry.merge(arrowShaft.geometry, arrowShaft.matrix);

arrowHead.updateMatrix();
arrowGeometry.merge(arrowHead.geometry, arrowHead.matrix);

var material = new THREE.MeshBasicMaterial( {
    color: 0x00aaaa,
    polygonOffset : true,
    polygonOffsetFactor : 1,
    polygonOffsetUnits : 1
});
var arrow = new THREE.Mesh(arrowGeometry, material);
scene.add(arrow);

var wireGeometry = new THREE.EdgesGeometry(arrow.geometry);
var wireMaterial = new THREE.LineBasicMaterial( { color: 0xffffff, linewidth: 2 });
var wireframe = new THREE.LineSegments( wireGeometry, wireMaterial );
arrow.add(wireframe);



function animate() {
    setTimeout( function()  {
        requestAnimationFrame( animate );
    }, 1000/30)
    
    arrow.rotation.x += 0.01;
    arrow.rotation.y += 0.02;
    arrow.rotation.z += 0.03;
	renderer.render( scene, camera );
}
animate();