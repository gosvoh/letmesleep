var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');

function initializeCoreMod() {
    return {
        'phantom_spawner_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.spawner.PhantomSpawner'
            },
            'transformer': function(classNode) {
                patch({
                    obfName: "func_203232_a",
                    name: "tick",
                    desc: "(Lnet/minecraft/world/server/ServerWorld;ZZ)I",
                    patch: patchPhantomSpawnerTick
                }, classNode, "PhantomSpawner");
                return classNode;
            }
        }
    };
}

function findMethod(methods, entry) {
    var length = methods.length;
    for(var i = 0; i < length; i++) {
        var method = methods[i];
        if ((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {
            return method;
        }
    }
    return null;
}

function patch(entry, classNode, name) {
    var method = findMethod(classNode.methods, entry);
    log("Patching " + name + "...");
    if(method !== null) {
        var obfuscated = method.name.equals(entry.obfName);
        entry.patch(method, obfuscated);
        log("Patching " + name + " was successful");
    } else {
        log("Patching " + name + " failed");
    }
}

function patchPhantomSpawnerTick(method, obfuscated) {
    var name = obfuscated ? "field_203233_a" : "ticksUntilSpawn";
    var insnList = new InsnList();
    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
    insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
    insnList.add(new VarInsnNode(Opcodes.ILOAD, 2));
    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
    insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/spawner/PhantomSpawner", name, "I"));
    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/fuzs/letmesleep/handler/InsomniaHandler", "checkInsomnia", "(Lnet/minecraft/world/server/ServerWorld;ZI)I", false));
    insnList.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/world/spawner/PhantomSpawner", name, "I"));
    method.instructions.insertBefore(method.instructions.getFirst(), insnList);
}

function log(s) {
    print("[Let Me Sleep Transformer]: " + s);
}