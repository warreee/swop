package be.swop.groep11.main.resource;

import be.swop.groep11.main.resource.constraint.ConflictCon;
import be.swop.groep11.main.resource.constraint.RequiresCon;
import be.swop.groep11.main.resource.constraint.TypeConstraint;
import com.google.common.collect.ImmutableList;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ronald on 13/04/2015.
 */
public class ResourceTypeBuilder {


    private RType type;

    public void newType(String typeName){

        this.type = new RType(typeName);
    }



    public void withDailyAvailability(DailyAvailability availability){
        this.type.setDailyAvailability(availability);
    }

    public void withDailyAvailability(LocalTime start,LocalTime end){
        withDailyAvailability(new DailyAvailability(start, end));
    }

    //TODO eventueel illegal constraint exception
    public void withConflictConstraint(IResourceType constrainingType)throws IllegalArgumentException{
        type.addConflictConstraint(constrainingType);
    }

    //Is dit oké?
    public void setConflictWithSelf(boolean conflict){
        if(conflict){
            withConflictConstraint(this.type);
        }
    }

    public void withRequirementConstraint(IResourceType constrainingType)throws IllegalArgumentException{
        type.addRequirementConstraint(constrainingType);
    }

    public void addResourceInstance(String instanceName){
        type.addResourceInstance(instanceName);
    }

    public IResourceType getResourceType(){
        return this.type;
    }

    private class RType implements IResourceType{

        private final String name;
        private DailyAvailability dailyAvailability;

        public RType(String typeName) throws  IllegalArgumentException{
            if(!isValidResourceTypeName(typeName)){
                throw new IllegalArgumentException("Ongeldige naam voor ResourceType");
            }
            this.name = typeName;
            this.dailyAvailability = new DailyAvailability(LocalTime.MIN,LocalTime.MAX);
        }

        public void setDailyAvailability(DailyAvailability dailyAvailability)throws  IllegalArgumentException {
            if(!isValidDailyAvailability(dailyAvailability)){
                throw new IllegalArgumentException("Ongeldige DailyAvailability");
            }
            this.dailyAvailability = dailyAvailability;
        }

        private boolean isValidDailyAvailability(DailyAvailability availability) {
            return availability != null;
        }

        /**
         * Controleert of de gegeven naam wel correct is.
         * @param name De naam die aan dit ResourceType gegeven wordt.
         * @return True als de naam niet null en niet leeg is.
         */
        public boolean isValidResourceTypeName(String name){
            return name != null && !name.isEmpty();
        }


        //ConstrainingType,constraint
        HashMap<IResourceType,TypeConstraint> constraintMap = new HashMap<>();

        public ImmutableList<TypeConstraint> getTypeConstraints() {
            return ImmutableList.copyOf(constraintMap.values());
        }

        //Verantwoordelijkheid toevoegen van constraints naar Repository?
        //TODO checkers?

        void addConflictConstraint(IResourceType constrainingType)throws IllegalArgumentException{
            if(!isValidConstrainingType(constrainingType)){
                throw new IllegalArgumentException("constrainingType mag niet null zijn");
            }
            TypeConstraint constraint = new ConflictCon(this,constrainingType);
            constraintMap.put(constrainingType,constraint);
        }

        void addRequirementConstraint(IResourceType constrainingType)throws IllegalArgumentException{
            if(!isValidConstrainingType(constrainingType)){
                throw new IllegalArgumentException("constrainingType mag niet null zijn");
            }
            TypeConstraint constraint = new RequiresCon(this,constrainingType);
            constraintMap.put(constrainingType,constraint);
        }

        private boolean isValidConstrainingType(IResourceType type){
            return type != null;
        }

        @Override
        public boolean hasConstraintFor(IResourceType typeA) {
            return constraintMap.containsKey(typeA);
        }

        @Override
        public TypeConstraint getConstraintFor(IResourceType typeA) {
            //TODO documentatie kan null terug geven
            return constraintMap.get(typeA);
        }

        @Override
        public int amountOfConstraints() {
            return constraintMap.size();
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public DailyAvailability getDailyAvailability() {
            return dailyAvailability;
        }


        /**
         * Een lijst die alle ResourceInstance van dit ResourceType bevat.
         */
        private ArrayList<ResourceInstance> instances = new ArrayList<>();

        /**
         * Voegt een nieuwe instantie van dit type resource toe aan deze ResourceType.
         * @param name De naam van de ResourceInstance die moet worden toegevoegd.
         */
        public void addResourceInstance(String name){
            //TODO controleren of het mogelijk is?
            Resource resource = new Resource(name, this);
            instances.add(resource);
        }

        @Override
        public ImmutableList<ResourceInstance> getResourceInstances() {
            return ImmutableList.copyOf(instances);
        }

        @Override
        public int amountOfInstances() {
            return instances.size();
        }
    }

}
