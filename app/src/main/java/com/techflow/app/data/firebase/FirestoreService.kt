package com.techflow.app.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.techflow.app.data.local.ProductEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// FirestoreService - encapsula las llamadas a Cloud Firestore para sincronizar el inventario
// El Repository usa este servicio, nunca el ViewModel directamente
// Estructura en Firestore: usuarios/{userId}/productos/{firestoreId}
// Cada usuario tiene su propio documento en "usuarios" y dentro su propia subcolección "productos"
@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // Nombre de la colección raíz donde se guarda un documento por cada usuario (UID)
    private companion object {
        const val COLLECTION_USUARIOS = "usuarios"
        const val SUBCOLLECTION_PRODUCTOS = "productos"
    }

    // Devuelve la referencia a la subcolección "productos" del usuario dado
    // usuarios/{userId}/productos
    private fun productosCollection(userId: String) =
        firestore.collection(COLLECTION_USUARIOS)
            .document(userId)
            .collection(SUBCOLLECTION_PRODUCTOS)

    // Genera un nuevo ID de documento de forma SINCRÓNICA, sin escribir nada en Firestore todavía
    // document() sin argumentos crea el ID en el cliente al instante, sin esperar a la red
    // Esto permite guardar el firestoreId en Room ANTES de escribir en la nube, para que el
    // addSnapshotListener (que se dispara casi al mismo tiempo que el set()) no encuentre
    // una fila local sin firestoreId y termine insertando un duplicado por condición de carrera
    fun generateFirestoreId(userId: String): String {
        return productosCollection(userId).document().id
    }

    // Inserta un producto en usuarios/{userId}/productos/{firestoreId}, usando un firestoreId
    // que ya fue generado previamente con generateFirestoreId()
    suspend fun insertProduct(userId: String, firestoreId: String, product: ProductEntity) {
        productosCollection(userId).document(firestoreId)
            .set(product.copy(firestoreId = firestoreId, userId = userId))
            .await()
    }

    // Actualiza el documento existente en usuarios/{userId}/productos/{firestoreId}
    // set() sobrescribe todos los campos del documento con los valores actuales del producto
    suspend fun updateProduct(userId: String, firestoreId: String, product: ProductEntity) {
        productosCollection(userId).document(firestoreId).set(product).await()
    }

    // Elimina el documento usuarios/{userId}/productos/{firestoreId}
    suspend fun deleteProduct(userId: String, firestoreId: String) {
        productosCollection(userId).document(firestoreId).delete().await()
    }

    // Obtiene todos los productos guardados en la nube para el usuario dado
    // get() trae todos los documentos de la subcolección y toObjects() los convierte en ProductEntity
    suspend fun getAllProducts(userId: String): List<ProductEntity> {
        val snapshot = productosCollection(userId).get().await()
        return snapshot.toObjects(ProductEntity::class.java)
    }

    // Escucha en tiempo real la subcolección de productos del usuario
    // addSnapshotListener (a diferencia de get()) se ejecuta automáticamente cada vez que
    // cambia algo en usuarios/{userId}/productos, sin tener que volver a llamar esta función
    // Retorna el ListenerRegistration para que quien lo registre pueda cancelarlo después
    // (remove()) y evitar fugas de memoria cuando ya no se necesite escuchar los cambios
    fun listenToProducts(
        userId: String,
        onUpdate: (List<ProductEntity>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return productosCollection(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.toObjects(ProductEntity::class.java) ?: emptyList()
                onUpdate(products)
            }
    }
}
