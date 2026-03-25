package com.sqool.sqoolbus.master.repository;

import com.sqool.sqoolbus.master.entity.ParentPupilMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentPupilMappingRepository extends JpaRepository<ParentPupilMapping, Long> {

    /**
     * Find all active pupil mappings for a parent by parent user ID
     */
    @Query("SELECT m FROM ParentPupilMapping m " +
           "WHERE m.parent.id = :parentId AND m.isActive = true")
    List<ParentPupilMapping> findAllActiveByParentId(@Param("parentId") Long parentId);

    /**
     * Find all pupil mappings for a parent by parent user ID (including inactive)
     */
    @Query("SELECT m FROM ParentPupilMapping m WHERE m.parent.id = :parentId")
    List<ParentPupilMapping> findAllByParentId(@Param("parentId") Long parentId);

    /**
     * Find all active pupil mappings for a parent in a specific tenant
     */
    @Query("SELECT m FROM ParentPupilMapping m " +
           "WHERE m.parent.id = :parentId AND m.tenant.tenantId = :tenantId AND m.isActive = true")
    List<ParentPupilMapping> findAllActiveByParentIdAndTenantId(
        @Param("parentId") Long parentId, 
        @Param("tenantId") String tenantId
    );

    /**
     * Find all active pupil mappings for a parent in a specific tenant and school
     */
    @Query("SELECT m FROM ParentPupilMapping m " +
           "WHERE m.parent.id = :parentId AND m.tenant.tenantId = :tenantId " +
           "AND m.schoolId = :schoolId AND m.isActive = true")
    List<ParentPupilMapping> findAllActiveByParentIdAndTenantIdAndSchoolId(
        @Param("parentId") Long parentId,
        @Param("tenantId") String tenantId,
        @Param("schoolId") Long schoolId
    );

    /**
     * Find all active pupil mappings for a school in a specific tenant
     */
    @Query("SELECT m FROM ParentPupilMapping m " +
           "WHERE m.tenant.tenantId = :tenantId AND m.schoolId = :schoolId AND m.isActive = true")
    List<ParentPupilMapping> findAllActiveByTenantIdAndSchoolId(
        @Param("tenantId") String tenantId,
        @Param("schoolId") Long schoolId
    );

    /**
     * Find a specific pupil mapping by parent, tenant, and pupil ID
     */
    @Query("SELECT m FROM ParentPupilMapping m " +
           "WHERE m.parent.id = :parentId AND m.tenant.tenantId = :tenantId AND m.pupilId = :pupilId")
    ParentPupilMapping findByParentIdAndTenantIdAndPupilId(
        @Param("parentId") Long parentId,
        @Param("tenantId") String tenantId,
        @Param("pupilId") Long pupilId
    );

    /**
     * Find a specific pupil mapping by parent, tenant, school, and student ID
     */
    @Query("SELECT m FROM ParentPupilMapping m " +
           "WHERE m.parent.id = :parentId AND m.tenant.tenantId = :tenantId " +
           "AND m.schoolId = :schoolId AND m.studentId = :studentId")
    ParentPupilMapping findByParentIdAndTenantIdAndSchoolIdAndStudentId(
        @Param("parentId") Long parentId,
        @Param("tenantId") String tenantId,
        @Param("schoolId") Long schoolId,
        @Param("studentId") String studentId
    );
}
