# ═══════════════════════════════════════════════════════════════════════════════
# StudyFlow - ProGuard/R8 Rules
# ═══════════════════════════════════════════════════════════════════════════════

# ───────────────────────────────────────────────────────────────────────────────
# CONFIGURACOES GERAIS
# ───────────────────────────────────────────────────────────────────────────────

# Manter informacoes de linha para stack traces legiveis
-keepattributes SourceFile,LineNumberTable

# Renomear arquivo fonte para ofuscar
-renamesourcefileattribute SourceFile

# Manter anotacoes (necessario para Room, etc)
-keepattributes *Annotation*

# Manter assinaturas genericas (necessario para Gson, etc)
-keepattributes Signature

# ───────────────────────────────────────────────────────────────────────────────
# ROOM DATABASE
# ───────────────────────────────────────────────────────────────────────────────

# Manter todas as classes anotadas com @Entity, @Dao, @Database
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }

# Manter classes de modelo (entidades)
-keep class com.example.gestaodetarefasestudos.models.** { *; }

# Manter DAOs
-keep class com.example.gestaodetarefasestudos.database.dao.** { *; }

# Manter converters do Room
-keep class com.example.gestaodetarefasestudos.database.Converters { *; }

# ───────────────────────────────────────────────────────────────────────────────
# ENUMS
# ───────────────────────────────────────────────────────────────────────────────

# Manter enums (usados pelo Room e serializacao)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.example.gestaodetarefasestudos.enums.** { *; }

# ───────────────────────────────────────────────────────────────────────────────
# VIEWMODELS E LIFECYCLE
# ───────────────────────────────────────────────────────────────────────────────

# Manter ViewModels (precisam ser encontrados por ViewModelProvider)
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class com.example.gestaodetarefasestudos.viewmodels.** { *; }

# ───────────────────────────────────────────────────────────────────────────────
# REPOSITORIES
# ───────────────────────────────────────────────────────────────────────────────

# Manter repositories e suas inner classes (callbacks)
-keep class com.example.gestaodetarefasestudos.repositories.** { *; }

# ───────────────────────────────────────────────────────────────────────────────
# BROADCAST RECEIVERS E SERVICES
# ───────────────────────────────────────────────────────────────────────────────

# Manter receivers (registrados no manifest)
-keep class com.example.gestaodetarefasestudos.receivers.** { *; }

# Manter services
-keep class com.example.gestaodetarefasestudos.services.** { *; }

# ───────────────────────────────────────────────────────────────────────────────
# PARCELABLE
# ───────────────────────────────────────────────────────────────────────────────

# Manter classes Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# ───────────────────────────────────────────────────────────────────────────────
# SERIALIZABLE
# ───────────────────────────────────────────────────────────────────────────────

# Manter membros de classes Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ───────────────────────────────────────────────────────────────────────────────
# MATERIAL COMPONENTS
# ───────────────────────────────────────────────────────────────────────────────

# Manter classes do Material Design
-keep class com.google.android.material.** { *; }

# ───────────────────────────────────────────────────────────────────────────────
# ENCRYPTED SHARED PREFERENCES
# ───────────────────────────────────────────────────────────────────────────────

# Manter classes de seguranca
-keep class androidx.security.crypto.** { *; }

# ───────────────────────────────────────────────────────────────────────────────
# REMOCAO DE LOGS EM RELEASE
# ───────────────────────────────────────────────────────────────────────────────

# Remover chamadas de Log.d e Log.v em release (manter Log.e e Log.w)
-assumenosideeffects class android.util.Log {
    public static int d(...);
    public static int v(...);
}

# ───────────────────────────────────────────────────────────────────────────────
# OTIMIZACOES
# ───────────────────────────────────────────────────────────────────────────────

# Permitir otimizacoes agressivas
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

# ───────────────────────────────────────────────────────────────────────────────
# AVISOS A IGNORAR
# ───────────────────────────────────────────────────────────────────────────────

# Ignorar avisos de bibliotecas externas
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
