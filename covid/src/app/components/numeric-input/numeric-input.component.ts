import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ErrorStateMatcher } from '@angular/material/core';
import { FormControl, FormGroupDirective, NgForm, Validators, AbstractControl, ValidatorFn, FormGroup } from '@angular/forms';

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

export function maxDecimalsValidator(maxDecimals: number): ValidatorFn {
  return (control: AbstractControl): { [key: string]: any } | null => {
    const value = control.value;
    let decimals = 0;
    if (value!=null && value!= undefined  && Math.floor(value) != value) {
      let splitted = value.toString().split(".");
      if (splitted.length == 1) decimals = 0;
      else decimals = splitted[1].length;
    }
    return decimals > maxDecimals ? { 'maxDecimals': { value: decimals } } : null;
  }
}

@Component({
  selector: 'app-numeric-input',
  templateUrl: './numeric-input.component.html',
  styleUrls: ['./numeric-input.component.scss']
})
export class NumericInputComponent implements OnInit  {
  @Input() required:boolean=false;
  @Input() model;
  @Input() label;
  @Input() min;
  @Input() max;
  @Input() placeholder;
  @Input() maxDecimals;
  @Input() identity;
  @Input() formGroup:FormGroup;
  @Input() step:number=1;
  @Input() pattern:string;
  @Input() textMode:boolean=false;
  @Input() usePercent:boolean=false;

  @Output() update: EventEmitter<string> = new EventEmitter<string>();


  inputFormControl;
  

  matcher = new MyErrorStateMatcher();
  
  constructor() { }

  ngOnInit(): void {
    let validators: ValidatorFn[] = [];
    if (!this.textMode && this.min != null)
      validators.push(Validators.min(this.min));
    if (!this.textMode && this.max != null)
      validators.push(Validators.max(this.max));
    if (this.required==true)
      validators.push(Validators.required);
    if (!this.textMode && this.maxDecimals != null)
      validators.push(maxDecimalsValidator(this.maxDecimals));

    if(this.identity==null)
      this.identity=this.label;

    this.inputFormControl = new FormControl('', validators);
    if(this.formGroup!=null){
      console.log("load fromGroup",this.identity);
      this.formGroup.removeControl(this.identity);
      this.formGroup.addControl(this.identity,this.inputFormControl);      
    }
  }

  handleChange() {
    if(this.inputFormControl.hasError('max') || this.inputFormControl.hasError('min') || this.inputFormControl.hasError('required'))
      return;
    console.log(this.model);
    this.update.emit(this.inputFormControl.value);
  }

  inputValidator(event: any) {
    if (this.pattern == null) return;
    const regex=new RegExp(this.pattern);
   
    let inputChar = String.fromCharCode(event.which);

    if (!regex.test(inputChar)) {
      // invalid character, prevent input
      event.preventDefault();
    }
  }
}
