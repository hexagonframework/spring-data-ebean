/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.ebean.repository.support;

import io.ebean.EbeanServer;
import org.springframework.data.domain.Persistable;
import org.springframework.data.ebean.repository.EbeanRepository;
import org.springframework.data.ebean.repository.query.EbeanQueryLookupStrategy;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.PersistableEntityInformation;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Optional;

/**
 * Ebean specific generic repository factory.
 *
 * @author Xuegui Yuan
 */
public class EbeanRepositoryFactory extends RepositoryFactorySupport {

    private final EbeanServer ebeanServer;

    /**
     * Creates a new {@link EbeanRepositoryFactory}.
     *
     * @param ebeanServer must not be {@literal null}
     */
    public EbeanRepositoryFactory(EbeanServer ebeanServer) {
        Assert.notNull(ebeanServer, "EbeanServer must not be null!");
        this.ebeanServer = ebeanServer;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.impl.RepositoryFactorySupport#setBeanClassLoader(java.lang.ClassLoader)
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        super.setBeanClassLoader(classLoader);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        return new PersistableEntityInformation(domainClass);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.impl.RepositoryFactorySupport#getTargetRepository(org.springframework.data.repository.core.RepositoryMetadata)
     */
    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
        return getTargetRepository(information, ebeanServer);
    }

    /**
     * Callback to create a {@link EbeanRepository} instance with the given {@link EbeanServer}
     *
     * @param <T>
     * @param <ID>
     * @param ebeanServer
     * @return
     */
    protected <T extends Persistable, ID extends Serializable> SimpleEbeanRepository<T, ID> getTargetRepository(
            RepositoryInformation information, EbeanServer ebeanServer) {

        return getTargetRepositoryViaReflection(information, information.getDomainType(), ebeanServer);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.impl.RepositoryFactorySupport#getRepositoryBaseClass(org.springframework.data.repository.core.RepositoryMetadata)
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleEbeanRepository.class;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.core.impl.RepositoryFactorySupport#getQueryLookupStrategy(org.springframework.data.repository.query.QueryLookupStrategy.Key, org.springframework.data.repository.query.EvaluationContextProvider)
     */
    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key,
                                                                   EvaluationContextProvider evaluationContextProvider) {
        return Optional.ofNullable(EbeanQueryLookupStrategy.create(ebeanServer, key, evaluationContextProvider));
    }

    /**
     * Returns whether the given repository interface requires a QueryDsl specific implementation to be chosen.
     *
     * @param repositoryInterface
     * @return
     */
    private boolean isQueryDslExecutor(Class<?> repositoryInterface) {
        return false;
    }
}
