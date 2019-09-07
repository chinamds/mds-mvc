package com.mds.sys.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import com.mds.core.exception.ArgumentNullException;

/// <summary>
/// A collection of <see cref="OrganizationBo" /> objects.
/// </summary>
@SuppressWarnings("serial")
public class OrganizationBoCollection extends ArrayList<OrganizationBo> {
	/// <summary>
	/// Initializes a new instance of the <see cref="OrganizationBoCollection"/> class.
	/// </summary>
	public OrganizationBoCollection(){
		super(new ArrayList<OrganizationBo>());
	}

	/// <summary>
	/// Sort the objects in this collection based on the <see cref="OrganizationBo.getOrganizationId()" /> property.
	/// </summary>
	@SuppressWarnings("unchecked")
	public void sort()	{
		// We know organizations is actually a List<OrganizationBo> because we passed it to the constructor.
		sort(new Comparator(){
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				if (((OrganizationBo)o2).getOrganizationId()>(((OrganizationBo)o1).getOrganizationId()))
					return -1;
				else if (((OrganizationBo)o2).getOrganizationId() < (((OrganizationBo)o1).getOrganizationId()))
					return 1;
				else
					return 0;
				
			}
		});
	}

	/// <summary>
	/// Adds the organizations to the current collection.
	/// </summary>
	/// <param name="organizations">The organizations to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="organizations" /> is null.</exception>
	public void AddRange(Iterable<OrganizationBo> organizations)	{
		if (organizations == null)
			throw new ArgumentNullException("organizations");

		addAll((Collection<? extends OrganizationBo>) organizations);
	}

	/// <summary>
	/// Find the organization in the collection that matches the specified <paramref name="organizationId" />. If no matching object is found,
	/// null is returned.
	/// </summary>
	/// <param name="organizationId">The ID that uniquely identifies the organization.</param>
	/// <returns>Returns an <see cref="OrganizationBo" />object from the collection that matches the specified <paramref name="organizationId" />,
	/// or null if no matching object is found.</returns>
	public OrganizationBo findById(long organizationId){
		return this.stream().filter(organization->organization.getOrganizationId() == organizationId).findFirst().orElse(null);
	}

	/// <summary>
	/// Creates a new, empty instance of an <see cref="OrganizationBo" /> object. This method can be used by code that only has a 
	/// reference to the interface layer and therefore cannot create a new instance of an object on its own.
	/// </summary>
	/// <returns>Returns a new, empty instance of an <see cref="OrganizationBo" /> object.</returns>
	public OrganizationBo createEmptyOrganizationBoInstance(){
		return new OrganizationBo();
	}

	/// <summary>
	/// Creates a new, empty instance of an <see cref="OrganizationBoCollection" /> object. This method can be used by code that only has a 
	/// reference to the interface layer and therefore cannot create a new instance of an object on its own.
	/// </summary>
	/// <returns>Returns a new, empty instance of an <see cref="OrganizationBoCollection" /> object.</returns>
	public OrganizationBoCollection createEmptyOrganizationBoCollection(){
		return new OrganizationBoCollection();
	}

	/// <summary>
	/// Creates a new collection containing deep copies of the items it contains.
	/// </summary>
	/// <returns>Returns a new collection containing deep copies of the items it contains.</returns>
	public OrganizationBoCollection copy(){
		OrganizationBoCollection copy = new OrganizationBoCollection();
		for (OrganizationBo organization : this){
			copy.add(organization.copy());
		}

		return copy;
	}
	
	/// <summary>
	/// Determines whether the <paramref name="item"/> is already a member of the collection. An object is considered a member
	/// of the collection if they both have the same <see cref="IOrganization.OrganizationId" />.
	/// </summary>
	/// <param name="item">An <see cref="IOrganization"/> to determine whether it is a member of the current collection.</param>
	/// <returns>Returns <c>true</c> if <paramref name="item"/> is a member of the current collection;
	/// otherwise returns <c>false</c>.</returns>
	public boolean contains(OrganizationBo item){
		if (item == null)
			return false;

		for (OrganizationBo organizationInCollection : this){
			if (organizationInCollection.getOrganizationId() == item.getOrganizationId()){
				return true;
			}
		}
		
		return false;
	}


	/// <summary>
	/// Adds the specified organization.
	/// </summary>
	/// <param name="item">The organization to add.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="item" /> is null.</exception>
	public void addOrganizationBo(OrganizationBo item)
	{
		if (item == null)
			throw new ArgumentNullException("item", "Cannot add null to an existing OrganizationBoCollection. Items.Count = " + size());

		add(item);
	}
}
