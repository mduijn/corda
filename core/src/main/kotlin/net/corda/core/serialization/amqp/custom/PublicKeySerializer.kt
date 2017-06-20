package net.corda.core.serialization.amqp.custom

import net.corda.core.crypto.Crypto
import net.corda.core.serialization.amqp.*
import org.apache.qpid.proton.codec.Data
import java.lang.reflect.Type
import java.security.PublicKey

/**
 * A serializer that writes out a public key as the encoded bytes and deserializes those encoded bytes.
 */
object PublicKeySerializer : CustomSerializer.Implements<PublicKey>(PublicKey::class.java) {
    override val additionalSerializers: Iterable<CustomSerializer<out Any>> = emptyList()

    override val schemaForDocumentation = Schema(listOf(RestrictedType(type.toString(), "", listOf(type.toString()), SerializerFactory.primitiveTypeName(ByteArray::class.java)!!, descriptor, emptyList())))

    override fun writeDescribedObject(obj: PublicKey, data: Data, type: Type, output: SerializationOutput) {
        // TODO: Instead of encoding to the default X509 format, we could have a custom per key type (space-efficient) serialiser.
        output.writeObject(obj.encoded, data, clazz)
    }

    override fun readObject(obj: Any, schema: Schema, input: DeserializationInput): PublicKey {
        val bits = input.readObject(obj, schema, ByteArray::class.java) as ByteArray
        return Crypto.decodePublicKey(bits)
    }
}