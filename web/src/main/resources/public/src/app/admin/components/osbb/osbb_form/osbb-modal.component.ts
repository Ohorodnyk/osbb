import { Component, Output, Input, EventEmitter, OnInit, ViewChild, ElementRef } from '@angular/core';
import { FORM_DIRECTIVES, CORE_DIRECTIVES, FormBuilder, Control, ControlGroup, Validators } from '@angular/common';
import { MODAL_DIRECTIVES, BS_VIEW_PROVIDERS, ModalDirective } from 'ng2-bootstrap/ng2-bootstrap';
import { TranslatePipe } from "ng2-translate";
import { CapitalizeFirstLetterPipe } from "../../../../../shared/pipes/capitalize-first-letter";
import { IOsbb, Osbb } from "../../../../../shared/models/osbb";
import { Attachment } from "../../../../../shared/models/attachment";
import { SelectItem } from "../../../../../shared/models/ng2-select-item.interface";
import { OsbbDTO } from '../osbb';
import { SELECT_DIRECTIVES } from "ng2-select";
import { Region, AddressDTO, Street } from "../../../../../shared/models/addressDTO";
import { AddressService } from '../../../../../shared/services/address.service';

@Component({
    selector: 'osbb-modal',
    templateUrl: './src/app/admin/components/osbb/osbb_form/osbb-modal.html',
    styleUrls: ['./src/app/admin/components/osbb/osbb_form/osbb-modal.css', './src/shared/css/general.css'],
    directives:[MODAL_DIRECTIVES, FORM_DIRECTIVES, CORE_DIRECTIVES, SELECT_DIRECTIVES],
    viewProviders: [BS_VIEW_PROVIDERS],
    providers: [AddressService],
    pipes: [TranslatePipe, CapitalizeFirstLetterPipe]
})
export class OsbbModalComponent implements OnInit{

    @Output() created: EventEmitter<OsbbDTO>;
    @Output() update: EventEmitter<OsbbDTO>;
    
    @ViewChild('modalWindow')
    modalWindow:ModalDirective;

    @ViewChild('inputLogo') 
    inputLogo: any;

    osbbDTO: OsbbDTO;
    osbb:IOsbb;
    isEditing:boolean;
    logoUrl:string;
    name: string;
    description: string;
    street: Street;
    address: string;
    district: string;
    available: boolean;
    regions: Array<any>;
    cities: Array<any>;
    streets: Array<any>;
    currentStreet: AddressDTO;
    currentRegion: AddressDTO;
    currentCity: AddressDTO;
    submitAttempt:boolean = false;
    creatingForm: ControlGroup;
    nameControl: Control;
    descriptionControl: Control;
    regionControl: Control;
    cityControl: Control;
    streetControl: Control;
    addressControl: Control;
    districtControl: Control;

     constructor(private builder: FormBuilder, private element: ElementRef, 
                 private addressService: AddressService) {
        this.osbbDTO = new OsbbDTO();
        this.created = new EventEmitter<OsbbDTO>();
        this.update = new EventEmitter<OsbbDTO>();
        this.nameControl = new Control('', Validators.required);
        this.descriptionControl = new Control('', Validators.required);
        this.regionControl = new Control('', Validators.required);
        this.cityControl = new Control('', Validators.required);
        this.streetControl = new Control('', Validators.required);
        this.addressControl = new Control('', Validators.required);
        this.districtControl = new Control('', Validators.required);
        this.creatingForm = builder.group({
            nameControl: this.nameControl,
            descriptionControl: this.descriptionControl,
            regionControl: this.regionControl,
            cityControl: this.cityControl,
            streetControl: this.streetControl,
            addressControl: this.addressControl,
            districtControl: this.districtControl,
        });

    }

     ngOnInit() {
        if(this.osbb === undefined){
            this.osbb = new Osbb();
        }
    }

    showLogo(event) {
        var reader = new FileReader();
        var image = this.element.nativeElement.querySelector('.image');
        reader.onload = function(e) {
            var src = e.target.result;
            image.src = src;
        };
        reader.readAsDataURL(event.target.files[0]);
    }

    openAddModal() {
        this.logoUrl = null;
        this.isEditing = false;
        this.regions = [];
        this.cities = [];
        this.streets = [];
        this.currentRegion = undefined;
        this.currentCity = undefined;
        this.currentStreet = undefined;
        this.fillRegions();
        this.modalWindow.show();  
    }

     openEditModal(osbb:IOsbb) {
        this.isEditing = true;
        this.osbb = osbb;
        this.name = osbb.name;
        this.description = osbb.description;
        this.street = osbb.street;
        this.address = osbb.address;
        this.district = osbb.district;
        this.available = osbb.available;
        if(osbb.logo !== null ) {
            this.logoUrl = osbb.logo.url;
        } else {
             this.logoUrl = '';
        }
        this.regions = [];
        this.cities = [];
        this.streets = [];
        this.currentRegion = undefined;
        this.currentCity = undefined;
        this.currentStreet = undefined;
        this.fillRegions();
        if (this.street != null) {
        this.fillCities(this.street.city.region.id);
        this.fillStreets(this.street.city.id);
        }
        this.modalWindow.show();
    }
    
    saveButtonAction(fileInput:any):void {
         this.submitAttempt = true;
         if(this.nameControl.valid && this.descriptionControl.valid 
                                && this.addressControl.valid && this.districtControl.valid) {
            let fileList: FileList = fileInput.files;
            if(this.osbbDTO.isChanged) {
                console.log("file was changed");
                 this.osbbDTO.file =  fileList.item(0);
            }
            if(this.isEditing) {
                console.log("isEditing");
                this.editOsbb();
                this.osbbDTO.osbb = this.osbb;
                this.update.emit(this.osbbDTO);
                
            } else {
               this.osbbDTO.osbb = this.createOsbb();
               this.created.emit(this.osbbDTO);
            }
            this.modalWindow.hide();
            this.clearForm();
        }
    }

    createOsbb():IOsbb {
        let osbb = new Osbb();
        osbb.name = this.name;
        osbb.description = this.description;
        osbb.street = this.street;
        osbb.address = this.address;
        osbb.district = this.district;
        osbb.creationDate = new Date();   
        return osbb;
    }

    toggleChangeLogo() {
        this.osbbDTO.isChanged = true;
    }

    editOsbb(): void {
           this.osbb.name = this.name;
           this.osbb.description = this.description;
           this.osbb.street = this.street;         
           this.osbb.address = this.address;
           this.osbb.district = this.district;
           this.osbb.available = this.available;
    }

    fillRegions(): void {
        this.addressService.getAllRegions().subscribe((data)=> {
             this.regions = [];
             for (let reg of data) {
                 let aDTO = new AddressDTO();
                 aDTO.id = reg.id;
                 aDTO.text = reg.name;
                 this.regions.push(aDTO);
                 if ((this.street != null) && (this.street.city.region.id == aDTO.id)) {
                     this.currentRegion = aDTO;
                 }
             }
             console.log("Regions List");
             console.log(this.regions);
             }, (error)=> {
                 this.handleErrors(error)
             });
    }
   
    fillCities(regionID: number): void {
        this.addressService.getAllCitiesOfRegion(regionID).subscribe((data)=> {
             this.cities = [];
             for (let cit of data) {
                 let aDTO = new AddressDTO();
                 aDTO.id = cit.id;
                 aDTO.text = cit.name;
                 this.cities.push(aDTO);
                 if ((this.street != null) && (this.street.city.id == aDTO.id)) {
                     this.currentCity = aDTO;
                 }
             }
             console.log("Cities List");
             console.log(this.cities);
             }, (error)=> {
                 this.handleErrors(error)
             });
    }

    fillStreets(cityID: number): void {
        this.addressService.getAllStreetsOfCity(cityID).subscribe((data)=> {
             this.streets = [];
             for (let str of data) {
                 let aDTO = new AddressDTO();
                 aDTO.id = str.id;
                 aDTO.text = str.name;
                 this.streets.push(aDTO);
                 if ((this.street != null) && (this.street.id == aDTO.id)) {
                     this.currentStreet = aDTO;
                 }
             }
             console.log("Streets List");
             console.log(this.streets);
             }, (error)=> {
                 this.handleErrors(error)
             });
    }

    selectedRegion(value: any): void {
        console.log(value);
        this.fillCities(value.id);
        this.streets = [];
    }

    selectedCity(value: any): void {
        this.fillStreets(value.id);
    }

    selectedStreet(value: any): void {
        this.addressService.getStreetById(value.id).subscribe((data)=> {
             this.street = data;
             console.log(this.street);
             }, (error)=> {
                 this.handleErrors(error)
             });
    }

     clearForm(): void {
        this.name='';
        this.description='';
        this.street = null;
        this.address='';
        this.district='';
        this.inputLogo.nativeElement.value = '';
        this.logoUrl='';
        this.submitAttempt = false;
    }
    handleErrors(error) {
        console.log('error msg' + error)
    }
}
