import { Component, OnInit, Input, Output,EventEmitter, AfterViewInit, HostListener, SimpleChange } from '@angular/core';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import { disableDebugTools } from '@angular/platform-browser';
import { MatButton } from '@angular/material/button';


@Component({
  selector: 'app-find-complete',
  templateUrl: './find-complete.component.html',
  styleUrls: ['./find-complete.component.scss']
})
export class FindCompleteComponent implements OnInit, AfterViewInit {
  @Input() options: string[];
  @Input() label: string;
  @Input() class: string;
  @Input() value:string;
  @Input() cleanAction:boolean;
  @Output() found: EventEmitter<string> = new EventEmitter<string>();

  innerControl: FormControl = new FormControl();
  filteredOptions: Observable<string[]>;
  disableOne:boolean=false;
  filterValue:string="";


  constructor() { }

  ngOnInit(): void {
    this.loadAutocomplete();
  }

  ngAfterViewInit():void{
    
  }

  clean(){
    console.log("clean")
    this.filterValue="";
    console.log(this.filterValue)
  }
    
  ngOnChanges(changes: SimpleChange[]) {
    //console.log("ngOnChanges",changes);
    this.loadAutocomplete();
    if(changes['cleanAction'])
      this.clean();
  }

  public loadAutocomplete(){
    this.filteredOptions = this.innerControl.valueChanges
    .pipe(
      startWith(''),
      map(value => this._filter(value))
    );
  }
  
 selectEvent(event){
    console.log("selectEvent",event);
    this.value = event.option.value;
    //this.selectValue(event.option.value);
    this.found.emit(this.innerControl.value);
  }

  
  private _filter(value: string) {
    console.log("_filter",this.filterValue)
    if(this.disableOne==false)
     { 
      this.filterValue = value.toLowerCase();
      this.disableOne=true;
     }
    else
    this.disableOne=false;
    
    Promise.resolve(null).then(() => this.found.emit(this.innerControl.value));
    return this.options.filter(option => option.toLowerCase().includes(this.filterValue));
  }


}
