/*
 * Copyright 2024 - Charles Dabadie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.demo.custom.hibernate;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.custom.hibernate.duplicate.JavaXProperty;
import io.github.jleblanc64.libcustom.custom.hibernate.duplicate.MyCollectionType;
import io.github.jleblanc64.libcustom.custom.utils.FieldCustomType;
import io.github.jleblanc64.libcustom.custom.utils.TypeImpl;
import io.github.jleblanc64.libcustom.meta.MetaList;
import io.github.jleblanc64.libcustom.meta.MetaOption;
import jakarta.persistence.metamodel.PluralAttribute;
import lombok.SneakyThrows;
import org.hibernate.boot.model.internal.AnnotationBinder;
import org.hibernate.boot.model.internal.BagBinder;
import org.hibernate.boot.model.internal.CollectionBinder;
import org.hibernate.boot.model.internal.PropertyInferredData;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.type.BagType;
import org.hibernate.type.CollectionType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import static io.github.jleblanc64.libcustom.custom.hibernate.Utils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.AnnotationException;
import org.hibernate.MappingException;
import org.hibernate.annotations.CollectionTypeRegistration;
import org.hibernate.annotations.CollectionTypeRegistrations;
import org.hibernate.annotations.CompositeTypeRegistration;
import org.hibernate.annotations.CompositeTypeRegistrations;
import org.hibernate.annotations.ConverterRegistration;
import org.hibernate.annotations.ConverterRegistrations;
import org.hibernate.annotations.EmbeddableInstantiatorRegistration;
import org.hibernate.annotations.EmbeddableInstantiatorRegistrations;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.FetchProfile;
import org.hibernate.annotations.FetchProfile.FetchOverride;
import org.hibernate.annotations.FetchProfiles;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.GenericGenerators;
import org.hibernate.annotations.Imported;
import org.hibernate.annotations.JavaTypeRegistration;
import org.hibernate.annotations.JavaTypeRegistrations;
import org.hibernate.annotations.JdbcTypeRegistration;
import org.hibernate.annotations.JdbcTypeRegistrations;
import org.hibernate.annotations.TypeRegistration;
import org.hibernate.annotations.TypeRegistrations;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.convert.spi.RegisteredConversion;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.resource.beans.internal.FallbackBeanInstanceProducer;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.type.descriptor.java.BasicJavaType;
import org.hibernate.type.descriptor.jdbc.JdbcType;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedStoredProcedureQueries;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SequenceGenerators;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.SqlResultSetMappings;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.TableGenerators;

import static org.hibernate.boot.model.internal.AnnotatedClassType.EMBEDDABLE;
import static org.hibernate.boot.model.internal.AnnotatedClassType.ENTITY;
import static org.hibernate.boot.model.internal.GeneratorBinder.buildGenerators;
import static org.hibernate.boot.model.internal.InheritanceState.getInheritanceStateOfSuperEntity;
import static org.hibernate.boot.model.internal.InheritanceState.getSuperclassInheritanceState;
import static org.hibernate.internal.CoreLogging.messageLogger;
import static org.hibernate.internal.util.StringHelper.unqualify;

public class VavrHibernate5 {
    @SneakyThrows
    public static void override(MetaList metaList) {
        var bagProvList = metaList.bag();

//        LibCustom.modifyArg(AnnotationBinder.class, "processElementAnnotations", 2, args -> {
//            var pid = (PropertyInferredData) args[2];
//            var p = pid.getProperty();
//            var type = (Type) getRefl(p, "type");
//            var at = (AccessType) getRefl(pid, "defaultAccess");
//            var rm = (ReflectionManager) getRefl(pid, "reflectionManager");
//            var j = JavaXProperty.of((JavaXMember) p, type, metaList);
//
//            if (!(type instanceof ParameterizedType))
//                return LibCustom.ORIGINAL;
//
//            var rawType = ((ParameterizedType) type).getRawType();
//            if (metaList.isSuperClassOf(rawType)) {
//                var f = (Field) j.getMember();
//                var jOver = JavaXProperty.of(f, type, j, metaList);
//                return new PropertyInferredData(pid.getDeclaringClass(), jOver, at.getType(), rm);
//            }
//
//            return LibCustom.ORIGINAL;
//        });

        LibCustom.override(org.hibernate.metamodel.internal.AttributeFactory.class, "determineCollectionType", args -> {
            var clazz = (Class) args[0];
            if (metaList.isSuperClassOf(clazz))
                return PluralAttribute.CollectionType.LIST;

            return LibCustom.ORIGINAL;
        });

        LibCustom.overrideWithSelf(org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder.class, "build", x -> {
            var self = x.self;

            var collectionClass = (Class) getRefl(self, "collectionClass");
            var listAttrClass = Class.forName("org.hibernate.metamodel.model.domain.internal.ListAttributeImpl");
            var constructor = listAttrClass.getDeclaredConstructor(PluralAttributeBuilder.class);
            constructor.setAccessible(true);

            if (metaList.isSuperClassOf(collectionClass))
                return constructor.newInstance(self);

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.type.CollectionType"), "getElementsIterator", 0, args -> {
            var collection = args[0];
            if (metaList.isSuperClassOf(collection))
                return metaList.toJava(collection);

            return collection;
        });

//        LibCustom.override(CollectionBinder.class, "getBinderFromBasicCollectionType", args ->
//                metaList.isSuperClassOf(args[0]) ? new BagBinder() : LibCustom.ORIGINAL);

        LibCustom.override(BagType.class, "instantiate", args -> {
            if (args.length == 1)
                return LibCustom.ORIGINAL;

            var pers = (AbstractCollectionPersister) args[1];
            if (isOfType(pers, metaList)){

            }
//                return checkPersistentBag(bagProvList.of((SharedSessionContractImplementor) args[0]));

            return LibCustom.ORIGINAL;
        });

//        LibCustom.override(BagType.class, "wrap", args -> {
//            var arg1 = args[1];
//
//            if (metaList.isSuperClassOf(arg1)) {
//                var c = metaList.toJava(arg1);
//                return checkPersistentBag(bagProvList.of((SharedSessionContractImplementor) args[0], c));
//            }
//
//            return LibCustom.ORIGINAL;
//        });

        LibCustom.overrideWithSelf(CollectionType.class, "replaceElements", x -> {
            var args = x.args;
            var c = (CollectionType) x.self;

            return MyCollectionType.replaceElements(args[0], args[1], args[2], (Map) args[3], (SharedSessionContractImplementor) args[4], c);
        });
    }

    @SneakyThrows
    public static void override(MetaOption<?> metaOption) {
        var setterFieldImplClass = Class.forName("org.hibernate.property.access.spi.SetterFieldImpl");
        var getterFieldImplClass = Class.forName("org.hibernate.property.access.spi.GetterFieldImpl");

        LibCustom.modifyArgWithSelf(setterFieldImplClass, "set", 1, argsSelf -> {
            var args = argsSelf.args;
            var value = args[1];
            var self = argsSelf.self;
            var field = (Field) getRefl(self, setterFieldImplClass.getDeclaredField("field"));

            if (metaOption.isSuperClassOf(field.getType()) && !metaOption.isSuperClassOf(value))
                return metaOption.fromValue(value);

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyReturn(getterFieldImplClass, "get", x -> {
            var ret = x.returned;
            if (metaOption.isSuperClassOf(ret))
                return metaOption.getOrNull(ret);

            return ret;
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.annotations.common.reflection.java.JavaXProperty"), "create", 0, args -> {
            var member = args[0];
            if (member instanceof Field) {
                var field = (Field) member;
                if (!(field.getGenericType() instanceof ParameterizedType))
                    return LibCustom.ORIGINAL;

                var type = (ParameterizedType) field.getGenericType();
                var typeRaw = type.getRawType();
                var typeParam = type.getActualTypeArguments()[0];
                var ownerType = ((ParameterizedType) field.getGenericType()).getOwnerType();

                if (metaOption.isSuperClassOf(typeRaw))
                    return FieldCustomType.create(field, new TypeImpl((Class<?>) typeParam, new Type[]{}, ownerType));
            }

            return LibCustom.ORIGINAL;
        });
    }
}
